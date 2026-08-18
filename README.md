# Foreman Backend

## Deployment can be found at
```
https://foreman-project.netlify.app/
```

A REST API backend for **Foreman**, a project/task management tool. Built with Spring Boot 3.5 (Java 21), it handles workspaces, projects, tasks, comments, memberships, and JWT-based authentication.

## Tech Stack

- **Java 21**, **Spring Boot 3.5.14**
- Spring Web, Spring Data JPA, Spring Security, Bean Validation
- **MySQL** (via `mysql-connector-j`)
- **JWT** auth (`jjwt` 0.12.7 — api/impl/jackson)
- **ModelMapper** for DTO ↔ entity mapping
- **springdoc-openapi** for Swagger/OpenAPI docs
- Maven build, multi-stage **Docker** image (Maven build → `eclipse-temurin:21-jre` runtime)

## Project Structure

```
src/main/java/com/foreman/
├── Application.java
├── configs/          # OpenApiConfig, SecurityConfig
├── controllers/       # REST endpoints
├── dtos/               # Request/response DTOs
├── entities/           # JPA entities
├── enums/              # ProjectRole, WorkspaceRole, TaskStatus, TaskPriority
├── exception/          # Global exception handling + custom exceptions
├── microservices/
│   └── notification/  # Client for an external notification microservice
├── repos/              # Spring Data JPA repositories
├── security/           # JWT filter, UserDetails, CustomUserDetailsService
└── services/           # Business logic
```

## Domain Model

The app is organized around **Workspaces → Projects → Tasks → Comments**, with membership/role tables controlling access:

- `Workspace`, `WorkspaceMembership` (roles via `WorkspaceRole`)
- `Project`, `ProjectMembership` (roles via `ProjectRole`)
- `Task` (with `TaskStatus`, `TaskPriority`)
- `Comment`
- `User`

## API Endpoints

All endpoints are prefixed with `/api`.

| Resource | Base path | Notes |
|---|---|---|
| Auth | `/api/auth` | `POST /register`, `POST /login` |
| Users | `/api/users` | `GET /`, `GET /{id}`, `GET /me`, `PUT /{id}/update`, `DELETE /{id}/delete` |
| Workspaces | `/api/workspaces` | `GET /`, `GET /{wrkspcId}`, `POST /`, `PUT /{wrkspcId}/update`, `DELETE /{wrkspcId}/delete` |
| Workspace members | `/api/workspaces/{wrkspcId}/members` | `GET /`, `POST /`, `GET /join`, `PUT /`, `DELETE /{memId}` |
| Projects | `/api/workspaces/{wrkspcId}/projects` | `GET /`, `GET /{projId}`, `POST /`, `PUT /{projId}/update`, `DELETE /{projId}/delete` |
| Project members | `/api/workspaces/{wrkspcId}/projects/{projId}/members` | `GET /`, `POST /`, `GET /join`, `PUT /`, `DELETE /{memId}` |
| Tasks | `/api/workspaces/{wrkspcId}/projects/{projId}/tasks` | `GET /`, `POST /`, `PUT /{taskId}`, `DELETE /{taskId}`, `GET /search` |
| Comments | `/api/workspaces/{wrkspcId}/projects/{projId}/tasks/{taskId}/comments` | `GET /`, `POST /`, `PUT /{commentId}`, `DELETE /{commentId}` |

Authentication is handled via JWT — the token issued at `/api/auth/login` must be sent as a `Bearer` token on subsequent requests. Interactive API docs are available via springdoc (Swagger UI), typically at `/swagger-ui.html` once the app is running.

## Configuration

The app is configured entirely through environment variables (see `src/main/resources/application.properties`):

| Variable | Required | Default | Purpose |
|---|---|---|---|
| `PORT` | No | `8080` | Server port |
| `SPRING_APPLICATION_NAME` | No | `Foreman_Backend` | App name |
| `SPRING_DATASOURCE_URL` | **Yes** | — | MySQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | **Yes** | — | DB username |
| `SPRING_DATASOURCE_PASSWORD` | **Yes** | — | DB password |
| `JWT_SECRET_KEY` | **Yes** | — | Secret used to sign JWTs |
| `JWT_EXPIRATION_MS` | No | `86400000` (24h) | JWT expiry |
| `APP_FRONTEND_DOMAIN` | **Yes** | — | Frontend origin, used for CORS/links (e.g. invite emails) |
| `NOTIFICATION_SERVICE_URL` | No | `http://localhost:5092` | URL of the external notification microservice |

Hibernate DDL mode is set to `update`, so the schema is created/updated automatically against the configured MySQL database — there are no separate migration scripts in this repo.

## Running Locally

### Prerequisites
- JDK 21
- MySQL instance (with a database created for the app)
- (Optional) A running notification microservice if you want notifications to work end-to-end

### Steps

```bash
# clone
git clone https://github.com/MVAbhra/Foreman.git
cd Foreman

# set required env vars, e.g.:
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/foreman"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="yourpassword"
export JWT_SECRET_KEY="a-long-random-secret"
export APP_FRONTEND_DOMAIN="http://localhost:5173"

# run
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080/api`.

### Run with Docker

```bash
docker build -t foreman-backend .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://<host>:3306/foreman" \
  -e SPRING_DATASOURCE_USERNAME="root" \
  -e SPRING_DATASOURCE_PASSWORD="yourpassword" \
  -e JWT_SECRET_KEY="a-long-random-secret" \
  -e APP_FRONTEND_DOMAIN="http://localhost:5173" \
  foreman-backend
```

### Tests

```bash
./mvnw test
```

## Related Repository

- Frontend: [Foreman Frontend](https://github.com/ashwathnakate/foreman)

