# Research: Vereinsweites Mannschaftsmanagement

**Date**: 2026-11-19
**Spec**: [spec.md](./spec.md)

## Decisions

### Backend runtime and build

- **Decision**: Use Java 21 LTS, Spring Boot 4.1.2, and the Maven Wrapper.
- **Rationale**: The user explicitly selected Java and Spring Boot. The current
  Spring Boot system requirements document lists 4.1.2 and supports Java 17+
  and Maven 3.6.3+; Java 21 is a supported LTS baseline. The wrapper keeps
  builds repeatable without requiring a machine-wide Maven installation.
- **Alternatives considered**: A TypeScript-only backend or a managed backend
  service would conflict with the selected backend platform. Gradle is also
  supported, but no existing Gradle convention exists in this project, so
  Maven Wrapper is the simpler starting point.

### Relational persistence and schema changes

- **Decision**: Use PostgreSQL 18, Spring Data JPA for ordinary persistence,
  Liquibase changelogs for schema changes, and Hibernate schema validation
  rather than automatic production schema generation. Use Spring Boot's default
  YAML master changelog with SQL changesets where PostgreSQL-specific features
  require them.
- **Rationale**: The feature depends on relational membership and history data,
  foreign-key relationships, unique active assignments, and atomic multi-row
  changes. PostgreSQL supports partial unique indexes and row-level locking.
  Spring Boot recommends using one schema-initialization mechanism; Liquibase
  changesets keep deployed schemas reproducible and match the user's experience.
  JPA can cover ordinary CRUD while targeted SQL and locks protect the few
  high-contention operations.
- **Alternatives considered**: A document database would make cross-entity
  integrity and history constraints harder to express. Flyway was the original
  migration choice, but Liquibase is preferred by the user. Hibernate
  `ddl-auto=update` is not used as a production migration strategy.

### Authentication, authorization, and browser security

- **Decision**: Use Spring Security with application-managed email/password
  sign-in and server-side HTTP sessions. Serve the SPA and API under one origin
  in production where possible. Keep CSRF protection enabled and use secure,
  HttpOnly, SameSite session cookies. Enable method-level authorization and
  enforce team membership in service operations as well as request handling.
- **Rationale**: This is a first-party browser application with no specified
  external identity provider. A session is simpler than introducing a separate
  token issuer; Spring Security documents session persistence, CSRF protection,
  adaptive password encoders, and explicit method security. The selected
  approach keeps team and family permissions on the trusted backend.
- **Alternatives considered**: JWT/OIDC can be added if a future identity
  provider or additional client requires it. It is not required by the current
  SRS. Client-side checks alone are explicitly insufficient.

### Transactional domain invariants

- **Decision**: Enforce invariants at both the database and service boundaries.
  Use unique constraints or partial unique indexes for one active team per
  player, one wash assignment per game, and one active coordinator per team.
  Allocate coach slots from the range 1–3 with a uniqueness constraint.
  Serialize catering-capacity changes by locking the catering task row.
  Perform player transfers in one Spring transaction, validating and locking
  all affected assignments before any updates.
- **Rationale**: Concurrent requests must not exceed limits or leave a partial
  roster transfer. PostgreSQL row locks coordinate concurrent writers, while
  database constraints provide a final integrity boundary if an application
  path is missed. All writers must use the same lock/transaction protocol.
- **Alternatives considered**: UI validation and check-then-write logic without
  a database lock are race-prone. A distributed lock or message broker is not
  warranted for a single-club application.

### Invitations and email delivery

- **Decision**: Store invitations as expiring, revocable, single-use records.
  Persist only a hash of a cryptographically random token; bind redemption to
  the invited address and apply the family/team membership in one transaction.
  Queue invitation messages in a database outbox and deliver them through
  Spring's mail abstraction using deployment-provided SMTP settings.
- **Rationale**: The token is a bearer credential and must not be stored or
  logged in plaintext. An outbox makes the invitation state and mail request
  durable together and allows delivery retries without a message-broker
  dependency. SMTP timeouts must be configured so an unavailable provider
  cannot block requests indefinitely.
- **Alternatives considered**: A third-party identity or email vendor is not
  selected because none is specified. The mail provider remains a deployment
  configuration, not a domain dependency.

### Test strategy

- **Decision**: Use Spring Boot Test with the Spring Boot-managed JUnit Jupiter
  version for backend tests and
  Testcontainers with real PostgreSQL for persistence, authorization,
  transaction, and concurrency tests. Add Vitest and React Testing Library for
  frontend unit/component behavior, and Playwright for browser-level
  acceptance flows.
- **Rationale**: Vitest reuses Vite configuration; React Testing Library
  emphasizes behavior through the rendered DOM; Playwright exercises complete
  browser workflows across supported browser engines. PostgreSQL-specific
  constraints and races must be tested against PostgreSQL rather than relying
  only on mocks or an in-memory substitute.
- **Alternatives considered**: Mock-only persistence tests cannot validate
  PostgreSQL constraints or locking. Browser tests alone are too slow and
  broad for every component rule. A separate Jest configuration would duplicate
  the existing Vite setup.

### Frontend and application boundary

- **Decision**: Keep the existing React 19, TypeScript 6, and Vite 8 application
  as the browser client; add a JSON REST API under `/api/v1` in the Spring Boot
  application. Use a Vite development proxy and a same-origin production
  routing arrangement.
- **Rationale**: This reuses the actual project baseline and keeps the browser
  interface responsive while moving authorization and persistence to the
  trusted backend. The supplied `ui-mocks/` provide initial guidance for the
  mobile parent navigation and administration areas.
- **Alternatives considered**: Moving the frontend into a monorepo subfolder
  provides no feature benefit and would create unrelated churn. A public
  third-party API is not required by the SRS.

## Resolved Assumptions

- The first deployment serves one club with multiple teams; cross-club SaaS
  tenancy is outside the current feature.
- No external identity provider or email provider is required by the SRS. The
  application owns account sign-in; production SMTP credentials are supplied
  through deployment configuration.
- No numeric user-count or request-latency target is given. Keep lists paged
  and preserve the measurable user outcomes in the spec; establish production
  capacity targets when the club's expected roster size is known.
- The production hosting provider is not specified. The API and frontend use
  environment-based configuration and standard HTTP so deployment can select
  a Java host, PostgreSQL host, and same-origin routing without changing
  domain behavior.
- Development uses test-only seed accounts and data. Production administrator
  bootstrap must be an explicit, one-time operator action; no default
  credentials are committed.

## Sources

- [Spring Boot system requirements](https://docs.spring.io/spring-boot/system-requirements.html)
- [Spring Security session management](https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html)
- [Spring Security CSRF protection](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html)
- [Spring Security password storage](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html)
- [Spring Security method security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- [Spring Framework transaction management](https://docs.spring.io/spring-framework/reference/data-access/transaction.html)
- [Spring Boot email sending](https://docs.spring.io/spring-boot/reference/io/email.html)
- [Spring Boot database initialization](https://docs.spring.io/spring-boot/how-to/data-initialization.html)
- [Spring Boot Testcontainers](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html)
- [PostgreSQL partial indexes](https://www.postgresql.org/docs/current/indexes-partial.html)
- [PostgreSQL explicit locking](https://www.postgresql.org/docs/current/explicit-locking.html)
- [Vitest getting started](https://vitest.dev/guide/)
- [React Testing Library introduction](https://testing-library.com/docs/react-testing-library/intro/)
- [Playwright introduction](https://playwright.dev/docs/intro)
