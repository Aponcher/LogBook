variable "app_version" {
  description = "App version"
  default     = "latest"
}

variable "region" {
  description = "AWS region"
  default     = "us-east-2"
}

variable "dockerhub_username" {
  description = "Docker Hub username or org name"
  type        = string
  default     = "aponcher"
}

variable "dockerhub_repo" {
  description = "Docker Hub repo name"
  type        = string
  default     = "logbook-app"
}

variable "image_tag" {
  description = "Docker image tag to deploy"
  type        = string
  default     = "latest"
}

variable "aws_account_id" {
  description = "AWS Account Id"
  type        = string
  default     = "694300052858"
}

variable "cloudflare_api_token" {
  description = "Cloudflare api token Id"
  type        = string
  default     = "wWlWjFA09HsW8naEaenZI2ZLVd2hZbAMO5XpDlij"
}