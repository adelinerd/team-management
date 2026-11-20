# Team Management

React/TypeScript frontend and Java 21 / Spring Boot 4.1.2 backend.

## Implementation status

The setup checkpoint and foundational entities, session/security endpoints,
local-only seed, generated TypeScript client, and packaged OpenAPI endpoint
are in place. Family authorization, import staging, the application shell,
and user-story APIs/UI remain outstanding. PostgreSQL/Testcontainers runtime
validation has not yet been confirmed. See the
[implementation tasks](specs/001-club-team-management/tasks.md) for status.

## Requirements

- Java 21 JDK (`JAVA_HOME` must point to it)
- Node.js 22.12+ and npm
- Podman Desktop with a started Podman machine and a Compose provider

On Windows, use `npm.cmd` if PowerShell execution policy blocks `npm.ps1`.

## Local services

Set `POSTGRES_DB`, `POSTGRES_USER`, and `POSTGRES_PASSWORD` in your shell using
local development values. Do not reuse production credentials or commit them.
These variables are required by Compose and by the backend's local profile.
A Compose `.env` file is not automatically loaded by Spring Boot.
No Docker Desktop or Docker Engine installation is required.

```powershell
podman machine list
podman info
podman compose version
podman compose up -d postgres mailpit
podman compose ps
```

Ports are bound to loopback: PostgreSQL 5432, SMTP 1025, mail UI 8025.
Optional overrides are `POSTGRES_PORT`, `SMTP_PORT`, and `MAILPIT_WEB_PORT`.
If no Podman machine is listed, create and start one in Podman Desktop before
running containers. `podman compose` requires a Compose provider.

Start the backend from a shell containing those variables:

```powershell
Set-Location backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

The local profile allows HTTP cookies and unauthenticated local SMTP only on
loopback. An optional local seed creates a club, season, team, and administrator
only when `LOCAL_SEED_ENABLED=true`; `LOCAL_ADMIN_EMAIL` and
`LOCAL_ADMIN_PASSWORD` must be supplied outside source control. Production
defaults require secure cookies, authenticated SMTP with STARTTLS, and
externally supplied `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`,
`SMTP_HOST`, `SMTP_USERNAME`, and `SMTP_PASSWORD`.

## Frontend

```powershell
npm.cmd ci
npm.cmd run validate:api
npm.cmd run generate:api
npm.cmd run dev
```

The dev, build, and frontend-test scripts generate the ignored client from the
canonical OpenAPI file automatically.

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

The generated backend startup test uses PostgreSQL 18 through Testcontainers.
For Testcontainers with Podman on Windows, set `DOCKER_HOST` to the Podman
machine pipe as described in the
[Podman Desktop guide](https://podman-desktop.io/docs/migrating-from-docker/using-the-docker_host-environment-variable).
Liquibase currently creates the foundation tables; remaining domain changesets
are pending. Hibernate validates rather than modifies schemas.
`.\mvnw.cmd -DskipTests package` checks compilation and packaging only, not
database startup or application behavior.

See [the implementation tasks](specs/001-club-team-management/tasks.md)
and [validation scenarios](specs/001-club-team-management/quickstart.md).

