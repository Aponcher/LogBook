resource "aws_security_group" "rds_sg" {
  name        = "rds-sg"
  description = "Allow Postgres access from ECS"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_security_group.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_instance" "logbook_db" {
  allocated_storage      = 20
  engine                 = "postgres"
  engine_version         = "15.12"
  instance_class         = "db.t3.micro"
  username               = "postgres"
  password               = "changeme123"
  identifier             = "loogbook-db"
  db_name                = "logbookdata"
  publicly_accessible    = false
  skip_final_snapshot    = true
  vpc_security_group_ids = [aws_security_group.rds_sg.id]
  db_subnet_group_name   = aws_db_subnet_group.default.name
  enabled_cloudwatch_logs_exports = [
    -"iam-db-auth-error",
    -"postgresql",
  ]
}

resource "aws_db_subnet_group" "default" {
  name       = "logbook-db-subnet-group"
  subnet_ids = aws_subnet.public[*].id
  tags = {
    Name = "Logbook DB Subnet Group"
  }
}

output "db_endpoint" {
  value = aws_db_instance.logbook_db.endpoint
}

output "db_username" {
  value = aws_db_instance.logbook_db.username
}
