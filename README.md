# Team Management

React/TypeScript frontend and Java 21 / Spring Boot 4.1.2 backend.

## Implementation status

The setup checkpoint is in place and foundation implementation has started.
Initial session/security endpoints, foundation entities and a Liquibase
changeset, shared error/audit helpers, and an endpoint serving the OpenAPI
contract exist. They are not yet feature-complete: family authorization,
integration tests, the generated TypeScript client, the application shell,
and user-story APIs/UI remain outstanding. See the
[implementation tasks](specs/001-club-team-management/tasks.md) for status.

## Requirements

- Java 21 JDK (`JAVA_HOME` must point to it)
- Node.js 22.12+ and npm
- Docker Desktop or a Docker-compatible runtime for PostgreSQL and backend tests

On Windows, use `npm.cmd` if PowerShell execution policy blocks `npm.ps1`.

## Local services

Set `POSTGRES_DB`, `POSTGRES_USER`, and `POSTGRES_PASSWORD` in your shell using
local development values. Do not reuse production credentials or commit them.
These variables are required by Compose and by the backend's local profile.
A Compose `.env` file is not automatically loaded by Spring Boot.

```powershell
docker compose up -d postgres mailpit
docker compose ps
```

Ports are bound to loopback: PostgreSQL 5432, SMTP 1025, mail UI 8025.
Optional overrides are `POSTGRES_PORT`, `SMTP_PORT`, and `MAILPIT_WEB_PORT`.

Start the backend from a shell containing those variables:

```powershell
Set-Location backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

The local profile allows HTTP cookies and unauthenticated local SMTP only on
loopback. Production defaults require secure cookies, authenticated SMTP with
STARTTLS, and externally supplied `DATABASE_URL`, `DATABASE_USERNAME`,
`DATABASE_PASSWORD`, `SMTP_HOST`, `SMTP_USERNAME`, and `SMTP_PASSWORD`.
The initial account bootstrap and session endpoints are present but have not
been integration-tested. No application seed accounts exist yet.

## Frontend

```powershell
npm.cmd ci
npm.cmd run dev
```

## Validation

```powershell
npm.cmd run lint
npm.cmd run build
npm.cmd run test:run
npm.cmd exec playwright install
npm.cmd run test:e2e
```

Frontend test runners are configured, but behavior tests and browser journeys
will be added with their implementation tasks. An empty test suite is not
considered a passing behavior test.

```powershell
Set-Location backend
.\mvnw.cmd verify
```

The generated backend startup test requires Docker and uses PostgreSQL 18.
Liquibase currently creates the foundation tables; remaining domain changesets
are pending. Hibernate validates rather than modifies schemas.
`.\mvnw.cmd -DskipTests package` checks compilation and packaging only, not
database startup or application behavior.

See [the implementation tasks](specs/001-club-team-management/tasks.md)
and [validation scenarios](specs/001-club-team-management/quickstart.md).

