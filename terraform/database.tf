resource "aws_db_subnet_group" "default" {
  name       = "logbook-db-subnet-group"
  subnet_ids = aws_subnet.public[*].id
  tags = {
    Name = "Logbook DB Subnet Group"
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
    "iam-db-auth-error",
    "postgresql",
  ]
}
