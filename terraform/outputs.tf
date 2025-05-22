output "ecs_cluster_id" {
  value = aws_ecs_cluster.main.id
}

output "cloudflare_zone_id" {
  value       = data.cloudflare_zones.primary.zones[0].id
  description = "The Cloudflare Zone ID used for DNS record creation."
}

output "cloudflare_zone_name" {
  value       = data.cloudflare_zones.primary.zones[0].name
  description = "The Cloudflare zone name fetched by the data source."
}

output "aws_acm_certificate_arn" {
  value       = aws_acm_certificate.api_cert.arn
  description = "The ARN of the ACM certificate."
}

output "aws_acm_certificate_status" {
  value       = aws_acm_certificate.api_cert.status
  description = "The current status of the ACM certificate."
}

output "aws_acm_certificate_domain_validation_options" {
  value       = aws_acm_certificate.api_cert.domain_validation_options
  description = "Details about the ACM certificate domain validation options."
}

output "alb_arn" {
  value       = aws_lb.logbook_alb.arn
  description = "The ARN of the Application Load Balancer."
}

output "alb_listeners" {
  value       = aws_lb_listener.https.*.arn
  description = "List of ALB listener ARNs."
}

output "domain_validation_options" {
  value = aws_acm_certificate.api_cert.domain_validation_options
}