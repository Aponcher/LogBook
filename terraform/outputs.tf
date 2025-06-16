output "cloudflare_zone_id" {
  value       = data.cloudflare_zones.primary.zones[0].id
  description = "The Cloudflare Zone ID used for DNS record creation."
}

output "cloudflare_zone_name" {
  value       = data.cloudflare_zones.primary.zones[0].name
  description = "The Cloudflare zone name fetched by the data source."
}

output "primary_zones_id" {
  value = data.cloudflare_zones.primary.zones[0].id
}

output "db_endpoint" {
  value = aws_db_instance.logbook_db.endpoint
}

output "db_username" {
  value = aws_db_instance.logbook_db.username
}


output "vpc_id" {
  value = data.aws_vpc.default.id
}

output "public_subnets" {
  value = aws_subnet.public[*].id
}

output "vpc_cidr_block" {
  value = data.aws_vpc.default.cidr_block
}

output "subnet_vpc_ids" {
  value = aws_subnet.public[*].vpc_id
}

output "apprunner_url" {
  value = aws_apprunner_service.logbook.service_url
}

output "apprunner_service_arn" {
  value = aws_apprunner_service.logbook.arn
}
