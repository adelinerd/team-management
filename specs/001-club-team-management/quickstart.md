# Quickstart: Vereinsweites Mannschaftsmanagement

This guide describes the local validation environment to be delivered with the
implementation. It does not contain production credentials or seed passwords.

## Prerequisites

- Java 21 JDK
- Node.js 22.12 or later and npm
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

4. In a second terminal, install frontend dependencies and start Vite:

   ```powershell
   npm ci
   npm run dev
   ```

5. Open the Vite local URL. Its `/api/v1` requests proxy to the local backend.
   The local administrator and invitation accounts are created by the
   development seed, not by production defaults. Invitation emails are visible
   only in the local mail catcher.

## Quality and Test Commands

Run from the repository root:

```powershell
npm run test:run
npm run lint
npm run build
```

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
   roster, match, and assignment data. No Team B data is returned or changed.
2. **Role limit**: As a club administrator, appoint three team managers and
   attempt a fourth appointment. The fourth action fails without changing
   existing appointments.
3. **Family invitation**: Invite two parent accounts for one family, accept
   both invitations, and verify both see the same family assignments. Expired,
   revoked, or reused tokens grant no access.
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

For the entity constraints and API failure behavior, see
[data-model.md](./data-model.md) and
[contracts/api-contract.md](./contracts/api-contract.md).
