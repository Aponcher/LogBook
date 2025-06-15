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
  required_providers {
    cloudflare = {
      source  = "cloudflare/cloudflare"
      version = "4.48.0"
    }
  }
}

provider "cloudflare" {
  api_token = var.cloudflare_api_token
}

resource "cloudflare_record" "ui" {
  zone_id = data.cloudflare_zones.primary.zones[0].id
  name    = "ui"
  type    = "CNAME"
  content = "silly-fenglisu-5601d3.netlify.app"
  proxied = false
}

# Proves ownership of netlify via static token
resource "cloudflare_record" "ui_netlify_verification" {
  zone_id = data.cloudflare_zones.primary.zones[0].id
  name    = "netlify-challenge" # Subdomain is included here
  type    = "TXT"
  content = "IrmuiFOi_9DuUFoIOK4N5"
  ttl     = 300
}

resource "cloudflare_record" "api" {
  zone_id = data.cloudflare_zones.primary.zones[0].id
  name    = "api"
  type    = "CNAME"
  content = replace(aws_apprunner_service.logbook.service_url, "https://", "")
  proxied = false
}

resource "cloudflare_record" "public_api" {
  zone_id = data.cloudflare_zones.primary.zones[0].id
  name    = "public-api"
  type    = "CNAME"
  content = replace(aws_apprunner_service.logbook.service_url, "https://", "")
  proxied = false
}

data "cloudflare_zones" "primary" {
  filter {
    name = "alponcher.us"
  }
}

resource "aws_iam_role" "apprunner_role" {
  name = "apprunner-instance-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "build.apprunner.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "apprunner_ecr" {
  role       = aws_iam_role.apprunner_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSAppRunnerServicePolicyForECRAccess"
}

resource "aws_apprunner_vpc_connector" "logbook_connector" {
  vpc_connector_name = "logbook-vpc-connector"
  subnets            = aws_subnet.public[*].id
  security_groups    = [aws_security_group.apprunner_sg.id]
}

resource "aws_apprunner_service" "logbook" {
  service_name = "logbook-apprunner"

  source_configuration {
    authentication_configuration {
      access_role_arn = aws_iam_role.apprunner_role.arn
    }

    auto_deployments_enabled = true

    image_repository {
      image_configuration {
        port = "8080"
        runtime_environment_variables = {
          SPRING_PROFILES_ACTIVE     = "dev"
          SPRING_DATASOURCE_URL      = "jdbc:postgresql://${aws_db_instance.logbook_db.endpoint}/logbookdata"
          SPRING_DATASOURCE_USERNAME = "postgres"
          SPRING_DATASOURCE_PASSWORD = "changeme123"
        }
      }

      image_identifier      = "${var.aws_account_id}.dkr.ecr.${var.region}.amazonaws.com/logbook-repository:v${var.app_version}"
      image_repository_type = "ECR"
    }
  }

  network_configuration {
    egress_configuration {
      egress_type       = "VPC"
      vpc_connector_arn = aws_apprunner_vpc_connector.logbook_connector.arn
    }
  }
}

resource "aws_security_group" "rds_sg" {
  name        = "rds-sg"
  description = "Allow Postgres access from ECS"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.apprunner_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "apprunner_sg" {
  name        = "apprunner-sg"
  description = "Security group for App Runner to access RDS"
  vpc_id      = data.aws_vpc.default.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
