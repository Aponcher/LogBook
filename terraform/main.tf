resource "aws_ecr_repository" "logbook" {
  name = "logbook-repository"
}

terraform {
  required_version = ">= 1.3"

  backend "remote" {
    organization = "logbook-org"

    workspaces {
      name = "logbook-infra"
    }
  }
}

provider "cloudflare" {
  email   = "aponcher@mac.com"
  api_key = "wWlWjFA09HsW8naEaenZI2ZLVd2hZbAMO5XpDlij"
}

resource "cloudflare_record" "api_tunnel" {
  zone_id = data.cloudflare_zones.primary[0].id
  name    = "api"                                                   # this makes api.alponcher.us
  value   = "72a5873f-2982-4c97-899c-52441c6b6e37.cfargotunnel.com" # replace with your actual tunnel hostname
  type    = "CNAME"
  ttl     = 300
  proxied = true
}

data "cloudflare_zones" "primary" {
  filter {
    name = "alponcher.us"
  }
}

resource "aws_lb" "logbook_alb" {
  name               = "logbook-alb"
  internal           = false
  load_balancer_type = "application"
  subnets            = aws_subnet.public[*].id
  security_groups    = [aws_security_group.alb_sg.id]
}

output "alb_dns_name" {
  description = "Public DNS name of the Application Load Balancer"
  value       = aws_lb.logbook_alb.dns_name
}

resource "aws_lb_target_group" "logbook_tg" {
  name        = "logbook-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = data.aws_vpc.default.id
  target_type = "ip"
  health_check {
    path                = "/actuator/health"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2
    matcher             = "200-399"
  }
}

resource "aws_acm_certificate" "api_cert" {
  domain_name       = "api.alponcher.us"
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_route53_record" "api_cert_validation" {
  name    = aws_acm_certificate.api_cert.domain_validation_options[0].resource_record_name
  type    = aws_acm_certificate.api_cert.domain_validation_options[0].resource_record_type
  zone_id = data.aws_route53_zone.main.zone_id
  records = [aws_acm_certificate.api_cert.domain_validation_options[0].resource_record_value]
  ttl     = 300
}

resource "aws_lb_listener" "https" {
  load_balancer_arn = aws_lb.logbook_alb.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = aws_acm_certificate.api_cert.arn

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.logbook_tg.arn
  }
}

resource "aws_lb_listener" "logbook_listener" {
  load_balancer_arn = aws_lb.logbook_alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.logbook_tg.arn
  }
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.logbook_alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "redirect"

    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

resource "aws_security_group" "ecs_security_group" {
  name        = "ecs-sg"
  description = "Allow inbound from ALB"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb_sg.id] # only allow traffic from ALB SG
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "ecs_service" {
  name        = "ecs_service_sg"
  description = "Security group for ECS service"
  vpc_id      = data.aws_vpc.default.id # Ensure this matches the VPC ID used for subnets
}

resource "aws_ecs_cluster" "main" {
  name = "logbook-cluster"
}

resource "aws_ecs_task_definition" "logbook" {
  family                   = "logbook-task"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 512
  memory                   = 1024

  container_definitions = jsonencode([
    {
      name      = "logbook"
      image     = "${var.aws_account_id}.dkr.ecr.${var.region}.amazonaws.com/logbook-repository:v${var.app_version}"
      essential = true
      memory    = 1024
      cpu       = 512
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.logbook.name
          awslogs-region        = var.region
          awslogs-stream-prefix = "ecs"
        }
      }
      environment = [
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = "dev"
        },
        {
          name  = "SPRING_DATASOURCE_URL"
          value = "jdbc:postgresql://${aws_db_instance.logbook_db.endpoint}/logbookdata"
        },
        {
          name  = "SPRING_DATASOURCE_USERNAME"
          value = "postgres"
        },
        {
          name  = "SPRING_DATASOURCE_PASSWORD"
          value = "changeme123"
        }
      ]
    }
  ])
}

resource "aws_ecs_service" "logbook_service" {
  name                 = "logbook-service"
  cluster              = aws_ecs_cluster.main.id
  task_definition      = aws_ecs_task_definition.logbook.arn
  desired_count        = 1
  launch_type          = "FARGATE"
  force_new_deployment = true

  network_configuration {
    subnets          = aws_subnet.public[*].id
    security_groups  = [aws_security_group.ecs_security_group.id]
    assign_public_ip = true # <--- This is key
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.logbook_tg.arn
    container_name   = "logbook"
    container_port   = 8080
  }
}

resource "aws_iam_role" "ecs_execution_role" {
  name = "ecs_execution_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
        Effect = "Allow"
        Sid    = ""
      },
    ]
  })
}

resource "aws_iam_role" "ecs_task_role" {
  name = "ecs_task_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
        Effect = "Allow"
        Sid    = ""
      },
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_ecr_pull" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
  role       = aws_iam_role.ecs_task_role.name
}

resource "aws_iam_role_policy_attachment" "ecs_execution_policy" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy_attachment" "ecs_task_policy" {
  role       = aws_iam_role.ecs_task_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_policy" "ecs_task_logging" {
  name = "ecs-task-logging-policy"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_logging_attachment" {
  role       = aws_iam_role.ecs_task_role.name
  policy_arn = aws_iam_policy.ecs_task_logging.arn
}

resource "aws_cloudwatch_log_group" "logbook" {
  name              = "/ecs/logbook"
  retention_in_days = 30
}

output "subnet_vpc_ids" {
  value = aws_subnet.public[*].vpc_id
}

output "sg_vpc_id" {
  value = aws_security_group.ecs_security_group.vpc_id
}