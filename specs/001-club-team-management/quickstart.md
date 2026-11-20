# Quickstart: Vereinsweites Mannschaftsmanagement

This guide describes the local validation environment to be delivered with the
implementation. It does not contain production credentials or seed passwords.

## Current Implementation Checkpoint (2026-11-20)

T001-T006, T008, and T010 implementation is in place. The API-first
`typescript-fetch` client is generated from the canonical contract, and
session/CSRF client tests pass. T007 still needs family-membership
authorization; T009 and T011 remain outstanding; T012 backend integration
coverage is incomplete. See the [task list](./tasks.md).

Java 21 clean packaging, frontend lint/build, OpenAPI validation, and focused
client tests pass. Docker-backed validation is not claimed: Podman Desktop is
installed, but the current CLI has no configured machine or connection. On
Windows, use `npm.cmd` if PowerShell blocks `npm.ps1`.

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
- Podman Desktop with a configured, running Podman machine and a Compose
  provider. Docker Desktop and Docker Engine are not required.
- A configured SMTP account for real invitation delivery; local development
  uses a disposable mail catcher

## Start the Application Locally

1. Confirm a Podman machine is configured. If none exists, create one in
   Podman Desktop. Start the configured machine and verify the CLI connection:

   ```powershell
   podman machine list
   podman machine start
   podman info
   podman compose version
   ```

2. Start the local PostgreSQL database and mail catcher from the repository
   root:

   ```powershell
   podman compose up -d postgres mailpit
   ```

3. Configure local backend values outside source control, including database
   URL/user/password and the mail-catcher host. Do not use production
   credentials in local files.
4. To create local club/team/admin fixtures, set `LOCAL_SEED_ENABLED=true` and
   provide `LOCAL_ADMIN_EMAIL` and `LOCAL_ADMIN_PASSWORD` outside source
   control. The local profile ignores those settings by default.
5. Start the backend. Liquibase applies the checked-in changelogs:

   ```powershell
   Set-Location backend
   .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
   ```

6. In a second terminal, install frontend dependencies and start Vite:

   ```powershell
   npm ci
   npm run dev
   ```

   The dev script generates the API client before starting Vite.

7. Open the Vite local URL. Its `/api/v1` requests proxy to the local backend.
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

The `validate:api` and `generate:api` scripts use the pinned generator
configuration and `specs/001-club-team-management/contracts/openapi.yaml` as
input. The output in
`src/generated/openapi/` is disposable, ignored by version control, and must
not be edited by hand. Regenerate after every contract change and before
frontend development, tests, or builds. The backend serves the same
specification to authenticated users at `GET /api/v1/openapi.yaml` as API
documentation. Contract tests
must check implemented responses and generated-client cookie/CSRF behavior.

Run backend tests, including PostgreSQL integration tests. On Windows, use
the Podman machine pipe as the Docker-compatible API endpoint for Testcontainers:

```powershell
$podmanPipe = podman machine inspect --format '{{.ConnectionInfo.PodmanPipe.Path}}'
$env:DOCKER_HOST = "npipe://$($podmanPipe -replace '\\', '/')"
```

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

The local services and validation scripts are configured. Remaining
domain-specific services, story APIs, and UI are tracked in [tasks.md](./tasks.md).

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
