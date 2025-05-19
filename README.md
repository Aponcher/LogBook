Logbook WebApp (Backend API)
============================

Overview
--------

This is the backend component of the Logbook App, a full-stack personal data tracker inspired by Star Trek-style
personal logs.

The API allows users to track:

- Quantitative activities such as pushups, situps, etc.
- More free-form thought logs (currently in progress)

Each log entry is timestamped and associated with a user (multi-user support planned). The data can later be visualized
or exported via the client app.

Tech Stack
----------

- Java 21
- Spring Boot
- PostgreSQL (RDS in dev, Docker locally)
- Dockerized (multi-target: Docker Hub + AWS ECR)
- Infrastructure as Code (Terraform)
- CI/CD: GitHub Actions

Local Development
-----------------

1. Start a local Postgres instance:

   docker run --name logbook-postgres -p 5432:5432 \
   -e POSTGRES_USER=user \
   -e POSTGRES_PASSWORD=dummy \
   -e POSTGRES_DB=logbook \
   -d postgres:15

2. Run the backend:

   ./mvnw spring-boot:run

3. Environment-specific configurations are handled via Spring profiles:
    - `default` for local
    - `dev` for ECS (loads RDS credentials)

4. API is exposed at:

   http://localhost:8080/api

API Endpoints (Sample)
----------------------

- POST /api/logs/pushups — Add a pushup log
- POST /api/logs/thought — Add a free-form text entry (in-progress)
- GET /api/logs/{type} — Fetch logs by type

Authentication/authorization is not enforced yet (roadmap item).

Testing
-------

- Tests are run automatically in CI against a service container (Postgres:15)
- Local: `./mvnw verify`
- Test user ID: `test-user` (set by test context)
- Integration tests clean up their own data

Infrastructure & Deployment
----------------------------

The project uses Terraform to provision:

- VPC, subnets
- RDS (PostgreSQL)
- ECS (Fargate) for the Spring Boot container
- ALB with HTTPS routing

Docker:

- Images are built on GitHub Actions and pushed to:
    - Docker Hub (public testing)
    - AWS ECR (production/staging)

CI/CD (GitHub Actions)
----------------------

CI runs on:

- Pushes to `main` or version tags (e.g., `v1.0.0`)
- Pull requests to `main`

Steps include:

- Build & test
- Docker build & push
- Deploy tagged images to ECR with `latest` and release-specific tags

Roadmap
-------

- Add support for:
    - Free-form log entries
    - Per-user authorization
    - Scheduled reminders
- Terraform module for frontend (S3 + CloudFront)
- Slack notifications on CI/CD completion
- Role-based access
- Admin audit logging

Repository
----------

All code and workflows are maintained in this monorepo.

