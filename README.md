# Multi-Tenant SaaS Platform

A backend for a multi-tenant SaaS application built with Spring Boot. Multiple organizations (tenants) can use the same system while keeping their data completely isolated and secure.

## Features

- **Multi-tenancy** — Each organization has isolated data; users can never access another tenant's information
- **Authentication** — JWT-based auth with secure password hashing (BCrypt)
- **Authorization** — Role-based access control (TENANT_ADMIN, MEMBER)
- **Projects & Tasks** — Tenant-scoped project management with task tracking
- **RESTful API** — Clean API design with proper status codes and error handling

## Tech Stack

- Java 17
- Spring Boot 3.5
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway (database migrations)
- JWT (JSON Web Tokens)
- Maven

## Getting Started

### Prerequisites

- Java 17+
- Docker (for PostgreSQL)

### Run PostgreSQL

```bash
docker run --name postgres -e POSTGRES_USER=mt_user -e POSTGRES_PASSWORD=mt_pass -e POSTGRES_DB=mt_saas -p 5432:5432 -d postgres
```

### Run the Application

```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`

## API Endpoints

### Health Check

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Health check |

### Tenants

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tenants` | Create a tenant |
| GET | `/api/tenants` | List all tenants |
| GET | `/api/tenants/{id}` | Get tenant by ID |

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT |

### Projects (requires authentication)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/projects` | Create a project |
| GET | `/api/projects` | List tenant's projects |
| GET | `/api/projects/{id}` | Get project by ID |

### Tasks (requires authentication)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/projects/{projectId}/tasks` | Create a task |
| GET | `/api/projects/{projectId}/tasks` | List project's tasks |
| GET | `/api/projects/{projectId}/tasks/{taskId}` | Get task by ID |
| PATCH | `/api/projects/{projectId}/tasks/{taskId}/status` | Update task status |

## Usage Examples

### Create a Tenant

```bash
curl -X POST http://localhost:8080/api/tenants \
  -H "Content-Type: application/json" \
  -d '{"name": "Acme Corp"}'
```

### Sign Up

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"tenantId": "<tenant-id>", "email": "user@acme.com", "password": "password123"}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@acme.com", "password": "password123"}'
```

### Create a Project (with JWT)

```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"name": "Website Redesign", "description": "Redesign company website"}'
```

### Create a Task

```bash
curl -X POST http://localhost:8080/api/projects/<project-id>/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"title": "Create wireframes", "description": "Design initial wireframes"}'
```

### Update Task Status

```bash
curl -X PATCH http://localhost:8080/api/projects/<project-id>/tasks/<task-id>/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"status": "IN_PROGRESS"}'
```

## Project Structure

```
src/main/java/com/kelee/multitenantsaas/
├── auth/                   # JWT and authentication
├── config/                 # Security config and exception handlers
├── project/                # Projects and tasks
│   └── api/                # Controllers and DTOs
├── tenant/                 # Tenant management
│   └── api/                # Controllers and DTOs
└── user/                   # User management
    └── api/                # Controllers and DTOs
```

## License

MIT