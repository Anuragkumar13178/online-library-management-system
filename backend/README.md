# Library API

Spring Boot 3 REST API targeting Java 17. Configuration is read from environment variables; see the root README.

Core endpoints: `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/books`, librarian book CRUD at `/api/books`, `POST /api/transactions/borrow`, `PUT /api/transactions/{id}/return`, `GET /api/transactions/my`, `/api/members`, `/api/notifications`, and `/api/reports/dashboard`. Authenticated requests use `Authorization: Bearer <token>`.

Hibernate creates/updates the schema for local development. For production, set `DDL_AUTO=validate` and use the included schema script with a migration tool.
