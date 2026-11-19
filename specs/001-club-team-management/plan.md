# Implementation Plan: Vereinsweites Mannschaftsmanagement

**Branch**: `001-club-team-management` | **Date**: 2026-11-19 | **Spec**: [spec.md](./spec.md)

**Input**: Product requirements from [spec.md](./spec.md), based on
[SRS_Mannschaftsmanagement.md](../../SRS_Mannschaftsmanagement.md).

## Summary

Deliver the responsive React application with a Java 21 / Spring Boot 4.1.2
REST backend and PostgreSQL 18 persistence. Keep authentication, authorization,
team-scoped reads and writes, invitation acceptance, and domain invariants on
the trusted backend. Use Spring Security sessions and CSRF protection for the
first-party browser client. Database constraints and transactional operations
protect player transfers, washing assignments, and catering capacity under
concurrent requests.

The current React/Vite project is only the starter screen and has no backend,
database, authentication, or test suite. The plan retains the existing frontend
at the repository root and adds a Maven backend, versioned database migrations,
and layered tests.

## Technical Context

**Language/Version**: Frontend TypeScript 6 / React 19; Node.js 22.12+;
backend Java 21 LTS / Spring Boot 4.1.2

**Primary Dependencies**: Existing React 19, React DOM, Vite 8, and TypeScript
6; Spring MVC, Spring Security, Spring Data JPA, Jakarta Validation, Liquibase,
PostgreSQL JDBC, and Spring mail support. Use the Maven Wrapper.

**Storage**: PostgreSQL 18; Liquibase-managed changelogs, with SQL changesets
where PostgreSQL-specific features are needed. Hibernate validates the migrated
schema and does not update production schemas.

**Testing**: Spring Boot Test with its managed JUnit Jupiter version;
Testcontainers with PostgreSQL for backend persistence and concurrency tests;
Vitest and React Testing Library for frontend unit/component behavior;
Playwright for browser acceptance tests.

**Target Platform**: Responsive modern browsers on smartphone, tablet, and
desktop; Java web service and PostgreSQL database. Route browser and API traffic
through one production origin where possible.

**Project Type**: Responsive single-page web application with a separate
Spring Boot REST service and relational database.

**Performance Goals**: Meet the user outcomes in the spec: a parent completes
an assignment within 60 seconds and identifies team/task status within
10 seconds in the stated user tests. Preserve every capacity and membership
invariant under concurrent writes. The SRS gives no throughput or API-latency
SLO; do not invent one before expected club volume is known.

**Constraints**: All access decisions and domain constraints are enforced by
the backend and database, not only by the UI. Keep parent workflows mobile
first and administrative workflows usable on tablet/desktop. Limit personal
data to the SRS. The first release serves one club; no numeric team or account
limit is specified. The frontend remains at the repository root. Hosting and
production email providers are not specified and must be configured outside
source control. No external identity provider is assumed.

**Scale/Scope**: One club, multiple teams and seasons, families, and players.
Use bounded/paged lists and transactional operations; establish hosting and
load targets from real club enrollment before production capacity planning.

## Constitution Check

*GATE: Pass before research; re-evaluated after Phase 1 design.*

| Constitution rule | Gate | Design evidence |
|---|---|---|
| Team-scoped authorization and least privilege | PASS | Spring Security request/method checks scope every operation to active club/team/family membership. Client-supplied IDs are never authorization. |
| Privacy by default | PASS | Store only SRS-required profile/contact fields; password hashes and raw invitation tokens are never returned by the API or logged. Store only a token hash in the invitation and a short-lived encrypted delivery payload in the mail outbox. |
| Domain integrity and traceable history | PASS | PostgreSQL constraints, row locks, and Spring transactions enforce caps, active player membership, assignment capacity, and all-or-nothing transfers. Historical rows are retained. |
| Mobile-first, role-aware usability | PASS | Parent navigation follows the supplied mobile mocks; active team and role remain visible; manager/coordinator/admin workspaces are separated. |
| Verifiable, focused changes | PASS | JUnit/Testcontainers cover backend policy and transaction boundaries; Vitest/Testing Library cover user-visible components; Playwright covers end-to-end flows. Existing lint/build remain gates. |
| Existing React/TypeScript/Vite baseline | PASS | Keep the current frontend and add only the Java backend and relational database required by the SRS's authentication, persistence, and server-side authorization requirements. |

**Initial Gate**: PASS. The SRS has no unresolved product clarification
markers. Technical decisions are documented in [research.md](./research.md).

**Post-design Gate**: PASS. The data model and API contract preserve
team-scoped authorization, server-enforced invariants, data minimization,
historical records, and the specified responsive workflows. No constitution
exceptions are required.

## Project Structure

### Documentation (this feature)

```text
specs/001-club-team-management/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── api-contract.md
└── tasks.md                  # Created by /speckit-tasks
```

### Source Code (repository root)

```text
src/                          # Existing Vite/React frontend root
├── main.tsx
├── app/                      # Application shell and route/workspace layout
├── components/               # Shared accessible UI
├── features/
│   ├── auth/
│   ├── administration/
│   ├── teams/
│   ├── families/
│   ├── players/
│   ├── matches/
│   └── assignments/
├── lib/
│   └── api/                  # Typed same-origin HTTP client
└── styles/

backend/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── src/
    ├── main/
    │   ├── java/de/teammanagement/
    │   │   ├── auth/
    │   │   ├── club/
    │   │   ├── teams/
    │   │   ├── families/
    │   │   ├── players/
    │   │   ├── matches/
    │   │   ├── assignments/
    │   │   ├── imports/
    │   │   ├── notifications/
    │   │   └── common/
    │   └── resources/db/changelog/
    │       └── db.changelog-master.yaml
    └── test/
        ├── java/de/teammanagement/
        └── resources/

tests/
└── e2e/                      # Playwright user journeys
```

**Structure Decision**: Retain the current root-level Vite application rather
than moving it into a new monorepo folder. Add one `backend/` Maven project
because the SRS requires trusted server-side identity checks and transactional
multi-record operations. Keep domain boundaries as packages within one Spring
Boot service; separate microservices or a second API gateway add no value for
the single-club scope.

The proposed package and feature directories describe implementation
boundaries; they do not imply that these directories already exist.

## Complexity Tracking

No constitution violations or exceptions require tracking. The additional
backend and database are the minimum justified by authentication, persistent
history, and server-enforced authorization and concurrency invariants; a
browser-only implementation would violate those requirements.
