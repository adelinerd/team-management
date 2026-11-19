# Quickstart: Vereinsweites Mannschaftsmanagement

This guide describes the local validation environment to be delivered with the
implementation. It does not contain production credentials or seed passwords.

## Current Implementation Checkpoint (2026-11-19)

T001-T004 setup is configured. Foundation implementation has started: initial
entities and a Liquibase changeset, session/security endpoints, shared
error/audit helpers, and the OpenAPI contract endpoint are present, but T005-
T007 and T010 remain incomplete. T008-T009, T011-T012, and all user stories are outstanding; see
the [task list](./tasks.md).

A clean backend package check succeeded with the configured Java 21 target,
with tests skipped. On this machine Java 21 is installed, but the current
`JAVA_HOME` still points to Java 17; set `JAVA_HOME` to Java 21 before Maven
commands. On Windows, `npm.cmd` avoids the machine's PowerShell restriction
on `npm.ps1`.

Frontend lint and production build pass, but there are no frontend behavior
tests yet. Docker-backed startup/integration validation is deferred at the
user's request; no container-backed result is claimed.

The commands and workflows below describe the intended completed application.
No seed accounts are provisioned. The current authentication and schema work
is foundational rather than feature-complete; remaining domain changesets,
OpenAPI client generation, and user stories are not yet implemented. An empty
frontend test suite is not a completed acceptance test. See the current setup
commands in [README.md](../../README.md).

## Prerequisites

- Java 21 JDK
- Node.js 22.12 or later and npm
- The pinned OpenAPI Generator CLI and generator version installed through the
  project's npm configuration; Java is required for generation
- Docker Desktop or another Docker-compatible runtime for PostgreSQL and
  Testcontainers
- A configured SMTP account for real invitation delivery; local development
  uses a disposable mail catcher

## Start the Application Locally

1. Start the local PostgreSQL database and mail catcher from the repository
   root:

   ```powershell
   docker compose up -d postgres mailpit
   ```

2. Configure local backend values outside source control, including database
   URL/user/password and the mail-catcher host. Do not use production
   credentials in local files.
3. Start the backend. Liquibase applies the checked-in changelogs; the local
   profile provides test-only club data and an explicit test administrator:

   ```powershell
   Set-Location backend
   .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
   ```

4. In a second terminal, install frontend dependencies, generate the API
   client from the maintained OpenAPI specification, and start Vite:

   ```powershell
   npm ci
   npm run generate:api
   npm run dev
   ```

5. Open the Vite local URL. Its `/api/v1` requests proxy to the local backend.
   The local administrator and invitation accounts are created by the
   development seed, not by production defaults. Invitation emails are visible
   only in the local mail catcher.

## Quality and Test Commands

Run from the repository root:

```powershell
npm run validate:api
npm run generate:api
npm run test:run
npm run lint
npm run build
```

The future `validate:api` and `generate:api` scripts must use the pinned
generator configuration and `specs/001-club-team-management/contracts/openapi.yaml`
as input. The output in
`src/generated/openapi/` is disposable, ignored by version control, and must
not be edited by hand. Regenerate after every contract change and before
frontend development, tests, or builds. The backend serves the same
specification to authenticated users at `GET /api/v1/openapi.yaml` as API
documentation. Contract tests
must check implemented responses and generated-client cookie/CSRF behavior.

Run backend tests, including PostgreSQL integration tests (Docker required):

```powershell
Set-Location backend
.\mvnw.cmd verify
```

Integration tests must apply the same Liquibase changelogs to PostgreSQL before
checking the schema and domain constraints.

Run end-to-end browser tests against the local services:

```powershell
Set-Location ..
npm run test:e2e
```

The implementation must add these scripts and local services; they are not
present in the current starter project yet.

## Acceptance Validation Scenarios

1. **Team access**: Sign in as a parent of Team A and request Team B's family,
   roster, match, and assignment data, including when the same family belongs
   to both teams but that parent was invited only to A. No Team B data is
   returned or changed, including through cross-team family history.
2. **Role limit**: As a club administrator, appoint three team managers and
   attempt a fourth appointment. The fourth action fails without changing
   existing appointments.
3. **Family invitation**: Invite two parent accounts for one family, accept
   both invitations, and verify both see the same family assignments. Expired,
   revoked, or reused tokens grant no access.
   An inactive family-team membership rejects acceptance without consuming the
   token; only an explicit team-manager reactivation permits subsequent acceptance.
4. **Concurrent capacity**: Attempt to claim the final washing or catering slot
   from two sessions at once. At most one succeeds and the configured capacity
   is never exceeded.
5. **Partial import**: Preview a file containing valid rows, invalid rows, and
   duplicates. Apply valid rows, retain row-level errors, and verify existing
   family task assignments are unchanged.
6. **Season transfer**: Preview a multi-player transfer, cancel and confirm no
   changes; repeat and confirm. Verify the transfer is all-or-nothing, prior
   player assignments remain in history, and family duties are not transferred.
7. **Mobile parent workflow**: On a narrow viewport, find the next game, claim
   a free family task, see the success confirmation, and identify the active
   team and updated task status.
8. **Administration and audit**: Edit club name/key/time zone as an
   administrator and reject a non-administrator. Manage families across teams;
   verify actor/time/record audit entries for membership changes, import rows,
   and transfers, with no successful-change audit entry for rolled-back changes.
9. **Coordinator eligibility**: Reject appointment of manager-only users or
   parents from another team. Accept an active parent with explicit access in
   this team; verify loss of eligibility blocks coordinator operations.
10. **Read-only archive**: Archive a season with incomplete duties. Verify
    authorized history remains available, current lists exclude those duties,
    and all roles are prevented from editing matches or changing assignments,
    including through imports and corrections.

For the entity constraints and API failure behavior, see
[data-model.md](./data-model.md) and
[contracts/api-contract.md](./contracts/api-contract.md).
