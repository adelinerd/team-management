---
description: "Implementation tasks for club-wide team management"
---

# Tasks: Vereinsweites Mannschaftsmanagement

**Input**: [spec.md](./spec.md), [plan.md](./plan.md), [research.md](./research.md), [data-model.md](./data-model.md), [API contract](./contracts/api-contract.md), [quickstart.md](./quickstart.md), and project constitution.

**Tests**: The spec requires measurable acceptance checks, including concurrency and user tests; the constitution requires tests for changed behavior and server-side authorization and invariant checks. Include the focused automated tests below and the specified manual usability study. Write each story's tests before its implementation and verify they fail for the intended missing behavior.

**Organization**: Tasks are grouped by story. Shared infrastructure is built first; dependencies and fixtures are explicit below. Paths are relative to the repository root and planned files need not exist yet. Constrained field citations refer to the named `Entity.field` in [data-model.md](./data-model.md). `specs/001-club-team-management/contracts/openapi.yaml` is the planned canonical API-first specification; frontend API calls use its generated TypeScript client, not handwritten endpoint definitions.

**Clarified implementation rules (2026-11-19)**: Each parent requires explicit family/team access. Only a team manager may explicitly reactivate inactive family-team membership before invitation acceptance. Club administrators manage families/memberships club-wide with audit, and edit the once-bootstrapped club profile. Coordinators must be active parents with explicit access in their team. Archived matches and duties are read-only history for all roles, including incomplete duties. Apply these rules to all API, import, and UI paths.

## Format: `[ID] [P?] [Story] Description`

- `[P]` marks tasks that can run concurrently in their stated dependency wave without editing the same files or relying on another unfinished task in that wave.
- Story tags map to the numbered stories in [spec.md](./spec.md). Unlabeled setup, foundation, and polish tasks are shared.
- Each task names the file or files it produces, changes, or validates.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Establish the frontend and backend build and local test environment without replacing the existing frontend.

- [X] T001 Create the Java 21 Spring Boot 4.1.2 Maven project and wrapper in `backend/pom.xml`, `backend/mvnw`, `backend/mvnw.cmd`, and `backend/src/main/java/de/teammanagement/TeamManagementApplication.java`, including web, validation, security, JPA, Liquibase, PostgreSQL, mail, Spring test, and Testcontainers dependencies.
- [X] T002 [P] Add Vitest, React Testing Library, Playwright, and `test:run` and `test:e2e` scripts to `package.json` and `package-lock.json`; configure `vite.config.ts` and `playwright.config.ts` for component and browser tests.
- [X] T003 [P] Define local PostgreSQL 18 and mail-catcher services in `compose.yaml` and non-secret local configuration in `backend/src/main/resources/application-local.yaml`; use externally supplied credentials and no production defaults.
- [X] T004 Configure PostgreSQL, Liquibase startup, Hibernate schema validation, session cookie and SMTP timeout settings in `backend/src/main/resources/application.yaml`; create `backend/src/main/resources/db/changelog/db.changelog-master.yaml` and a test-only profile in `backend/src/test/resources/application.yaml`.

**Checkpoint**: Maven and npm tooling are configured; PostgreSQL and the mail catcher can be started locally.

**Execution status (2026-11-19)**: T001 validated with Java 21 compilation and
packaging (`-DskipTests package`); T002 validated with lint, TypeScript/Vite
build, Vitest configuration loading, Playwright version, and a jsdom runtime
probe. No frontend behavior tests exist yet. T003/T004 configuration passed
static YAML validation. `mvnw.cmd verify` failed because Testcontainers could
not find a Docker environment. On 2026-11-19 the user explicitly authorized
proceeding while deferring tests and Docker validation; no container-backed
result is claimed.

**Foundation progress (2026-11-19)**: Initial code exists for T005-T007 and
the contract-serving portion of T010, but those tasks remain incomplete.
T008-T009 and T011-T012 are unstarted. A clean backend package check passed
with Java 21 and tests skipped. Frontend lint/build pass; there are no behavior
tests yet.

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Create shared identity, authorization, error handling, import preview, and application shell foundations. Complete before story implementation.

- [ ] T005 Add Liquibase changesets and JPA mappings for club, season, user, club role, team, team role, and audit records in `backend/src/main/resources/db/changelog/001-foundation.yaml` and `backend/src/main/java/de/teammanagement/common/`; implement [data-model.md](./data-model.md) fields `Club.name`, `Club.externalKey`, `Club.timeZone`, `Season.name`, `Season.status`, `Team.name`, `Team.description`, `Team.status`, `UserAccount.email`, `UserAccount.passwordHash`, `UserAccount.displayName`, `UserAccount.status`, `ClubRole.role`, `TeamRoleAssignment.role`, `TeamRoleAssignment.managerSlot`, and `AuditEvent.details` with canonical constraints and relationships. Also implement identity, ownership, and UTC timing per [data-model.md](./data-model.md): `Club.id`, `Club.createdAt`, `Club.updatedAt`, `Season.id`, `Season.clubId`, `Season.startDate`, `Season.endDate`, `Team.id`, `Team.clubId`, `Team.seasonId`, `Team.createdAt`, `Team.updatedAt`, `UserAccount.id`, `UserAccount.createdAt`, `ClubRole.clubId`, `ClubRole.userId`, `ClubRole.createdAt`, `ClubRole.revokedAt`, `TeamRoleAssignment.id`, `TeamRoleAssignment.teamId`, `TeamRoleAssignment.userId`, `TeamRoleAssignment.active`, `TeamRoleAssignment.appointedBy`, `TeamRoleAssignment.createdAt`, `TeamRoleAssignment.revokedAt`, `AuditEvent.id`, `AuditEvent.clubId`, `AuditEvent.actorUserId`, `AuditEvent.eventType`, `AuditEvent.entityType`, `AuditEvent.entityId`, and `AuditEvent.occurredAt`.
- [ ] T006 Create `specs/001-club-team-management/contracts/openapi.yaml` with OpenAPI 3.0 definitions for the foundational session, CSRF, errors, authenticated documentation endpoint, and shared security schemes before implementing Spring Security session authentication, adaptive password hashing, CSRF token endpoint and rotation, logout, session fixation protection, and an explicit one-time admin bootstrap without committed credentials in `backend/src/main/java/de/teammanagement/auth/SecurityConfig.java`, `SessionController.java`, and `AdminBootstrap.java`.
- [ ] T007 Implement deny-by-default club/team/family authorization helpers, team-scoped query rules, generic errors with safe correlation IDs, and secret-safe audit recording in `backend/src/main/java/de/teammanagement/common/AccessPolicy.java`, `ApiExceptionHandler.java`, and `AuditService.java`; family-dependent checks must resolve current membership rather than trust client IDs.
- [ ] T008 Create local-only bootstrap fixtures and a PostgreSQL Testcontainers base in `backend/src/main/java/de/teammanagement/common/LocalSeed.java` and `backend/src/test/java/de/teammanagement/PostgresIntegrationTest.java`; apply the production Liquibase changelog and keep seeded accounts and secrets out of production.
- [ ] T009 Build the common short-lived CSV/JSON import staging, field mapping, preview/apply and per-row error framework in `backend/src/main/resources/db/changelog/002-imports.yaml` and `backend/src/main/java/de/teammanagement/imports/ImportBatchService.java`; implement [data-model.md](./data-model.md) fields `ImportBatch.id`, `ImportBatch.teamId`, `ImportBatch.createdBy`, `ImportBatch.importType`, `ImportBatch.status`, `ImportBatch.stagedRows`, `ImportBatch.expiresAt`, and `ImportBatch.appliedAt`, and bind each preview to its creator and team, revalidate on apply, and purge staged personal data on apply or expiry.
- [ ] T010 Configure pinned OpenAPI Generator CLI and `typescript-fetch` in `package.json`, `package-lock.json`, and `openapitools.json`, with `validate:api` and `generate:api` scripts using `specs/001-club-team-management/contracts/openapi.yaml` to generate into `src/generated/openapi/` (exclude generated files in `.gitignore` and `eslint.config.js`). Package the same source contract via `backend/pom.xml` and serve it to authenticated users at `GET /api/v1/openapi.yaml` in `backend/src/main/java/de/teammanagement/common/OpenApiDocumentationController.java`. Implement the generated-client transport adapter for same-origin credentials, CSRF token/header acquisition and renewal after session changes, safe error handling without automatic mutation replay, and Vite `/api/v1` proxy in `src/lib/api/client.ts` and `vite.config.ts`.
- [ ] T011 Build the responsive application shell, login form, routes, basic role-aware workspace navigation and keyboard-accessible shared controls in `src/app/AppShell.tsx`, `src/features/auth/LoginPage.tsx`, `src/components/`, and `src/App.tsx`; use `ui-mocks/` as visual guidance.
- [ ] T012 Add foundational authorization, CSRF/login/logout, Liquibase startup, authenticated access to the packaged documentation, and import ownership/expiry tests in `backend/src/test/java/de/teammanagement/common/FoundationIntegrationTest.java` and generated-client session/CSRF/error tests in `src/lib/api/client.test.ts` and `src/features/auth/LoginPage.test.tsx`; validate actual backend status codes and bodies against `specs/001-club-team-management/contracts/openapi.yaml`.

**Checkpoint**: Authenticated test users can access only permitted contexts; a fresh PostgreSQL instance migrates and the shell connects to the API. Story-specific authorization and behavior still require their own tests.

## Phase 3: User Story 1 - Verein und Mannschaften verwalten (Priority: P1) 🎯

**Goal**: Enable club administrators to create, edit, deactivate, and oversee teams and appoint or revoke up to three managers.

**Independent Test**: Create and deactivate a team, appoint three managers and reject a fourth, verify unauthorized callers are denied, and inspect actor/time audit records.

### Tests for User Story 1

- [ ] T013 [US1] Define team, club profile read/edit, season-listing, and manager-appointment operations and schemas first in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add PostgreSQL-backed club-profile name/key/time-zone validation and authorization, concurrent manager-limit, audit, and OpenAPI response-conformance tests in `backend/src/test/java/de/teammanagement/teams/TeamAdministrationIntegrationTest.java`.
- [ ] T014 [P] [US1] Add club administration form and access-denial tests in `src/features/administration/TeamAdministration.test.tsx`.

### Implementation for User Story 1

- [ ] T015 [US1] Implement club/team/season listing, club profile read/edit after one-time bootstrap, team create/edit/deactivate, and bounded manager appointment/revocation in `backend/src/main/java/de/teammanagement/teams/TeamAdministrationService.java`, `TeamAdministrationController.java`, and `backend/src/main/java/de/teammanagement/club/ClubController.java`; use [data-model.md](./data-model.md) `Club.name`, `Club.externalKey`, `Club.timeZone`, `TeamRoleAssignment.role`, `TeamRoleAssignment.managerSlot`, `TeamRoleAssignment.active`, `Team.status`, and `AuditEvent.details`, and serialize concurrent appointments.
- [ ] T016 [US1] Implement club overview and editable name/key/time-zone profile, team form, status controls, manager selection, and visible errors in `src/features/administration/TeamAdministration.tsx` and `src/features/administration/teamApi.ts`.
- [ ] T017 [US1] Exercise the US1 independent test and audit/denial checkpoint using `tests/e2e/club-administration.spec.ts` with local seeded users.

**Checkpoint**: US1 is demonstrable using local fixtures without implementing the other stories.

## Phase 4: User Story 2 - Spieler, Familien und Einladungen verwalten (Priority: P1)

**Goal**: Manage families and players and invite parents securely to the appropriate team and family.

**Independent Test**: Create one family with multiple players, invite two parents, accept both invitations, verify shared family access and team isolation, reject expired/revoked/reused invitations, and preview invalid import rows.

### Tests for User Story 2

- [ ] T018 [US2] Define family, player, invitation, parent-team access, explicit manager reactivation, and family/player import operations and schemas first in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add team-scoped and club-wide family/player CRUD endpoint tests (including unassigned club records), active-assignment uniqueness, team isolation, deactivation/reactivation/history, partial import, transactional membership audit, and OpenAPI response-conformance tests in `backend/src/test/java/de/teammanagement/families/FamilyPlayerIntegrationTest.java`.
- [ ] T019 [P] [US2] Add invitation registration/acceptance, recipient verification, expiry, revocation, replay, concurrent acceptance, inactive family-team rejection without token consumption, concurrent deactivation/acceptance, successful acceptance after explicit manager reactivation, and OpenAPI response-conformance tests in `backend/src/test/java/de/teammanagement/auth/InvitationIntegrationTest.java`; prove inviting a parent to Team A grants no access to Team B even when the family belongs to both teams.
- [ ] T020 [P] [US2] Add family management and invitation UI tests in `src/features/families/FamilyManagement.test.tsx`.

### Implementation for User Story 2

- [ ] T021 [US2] Add family/member, player, and player-team-assignment Liquibase changesets and mappings in `backend/src/main/resources/db/changelog/003-families-players.yaml` and `backend/src/main/java/de/teammanagement/families/Family.java` and `backend/src/main/java/de/teammanagement/players/Player.java`; implement [data-model.md](./data-model.md) fields `Family.id`, `Family.clubId`, `Family.familyName`, `Family.contactName`, `Family.contactEmail`, `Family.status`, `FamilyMembership.id`, `FamilyMembership.familyId`, `FamilyMembership.teamId`, `FamilyMembership.status`, `FamilyMembership.createdAt`, `FamilyMembership.deactivatedAt`, `FamilyMember.familyId`, `FamilyMember.userId`, `FamilyMember.status`, `FamilyMember.createdAt`, `FamilyMember.removedAt`, `Player.id`, `Player.clubId`, `Player.familyId`, `Player.name`, `Player.status`, `Player.createdAt`, `Player.updatedAt`, `PlayerTeamAssignment.id`, `PlayerTeamAssignment.playerId`, `PlayerTeamAssignment.teamId`, `PlayerTeamAssignment.seasonId`, `PlayerTeamAssignment.startDate`, `PlayerTeamAssignment.endDate`, `PlayerTeamAssignment.status`, and `PlayerTeamAssignment.createdAt`, including the active-player partial unique index and team/season relationships.
- [ ] T022 [US2] Add explicit parent-access schema/mapping in `backend/src/main/resources/db/changelog/003-families-players.yaml` and `backend/src/main/java/de/teammanagement/families/ParentTeamAccess.java` per [data-model.md](./data-model.md) `ParentTeamAccess.id`, `ParentTeamAccess.userId`, `ParentTeamAccess.familyId`, `ParentTeamAccess.teamId`, `ParentTeamAccess.status`, `ParentTeamAccess.grantedAt`, and `ParentTeamAccess.revokedAt`. Implement team-scoped and club-wide family/player CRUD and filters, parent-access administration, family-team deactivation and explicit team-manager reactivation, and transactional actor/time/record audit for membership changes in `backend/src/main/java/de/teammanagement/families/FamilyService.java` and `backend/src/main/java/de/teammanagement/players/PlayerService.java`. Expose team-scoped and club-wide HTTP operations (including unassigned records) in `backend/src/main/java/de/teammanagement/families/FamilyController.java` and `backend/src/main/java/de/teammanagement/players/PlayerController.java`; implement family/player CSV/JSON adapters and HTTP preview/apply in `backend/src/main/java/de/teammanagement/imports/FamilyPlayerImport.java` and `ImportController.java`, with per-valid-row transactional audit. Update `backend/src/main/java/de/teammanagement/common/AccessPolicy.java` to check explicit parent grants separately from administrative permissions.
- [ ] T023 [US2] Add invitation and encrypted email-outbox Liquibase changesets and mappings in `backend/src/main/resources/db/changelog/004-invitations.yaml` and `backend/src/main/java/de/teammanagement/auth/Invitation.java`; implement [data-model.md](./data-model.md) fields `Invitation.id`, `Invitation.clubId`, `Invitation.teamId`, `Invitation.familyId`, `Invitation.invitedEmail`, `Invitation.invitedBy`, `Invitation.tokenHash`, `Invitation.status`, `Invitation.createdAt`, `Invitation.expiresAt`, `Invitation.acceptedAt`, `Invitation.revokedAt`, `EmailOutboxItem.id`, `EmailOutboxItem.invitationId`, `EmailOutboxItem.encryptedPayload`, `EmailOutboxItem.status`, `EmailOutboxItem.attemptCount`, `EmailOutboxItem.nextAttemptAt`, `EmailOutboxItem.sentAt`, and `EmailOutboxItem.lastErrorCode`.
- [ ] T024 [US2] Implement invite/revoke/status, configurable expiry, existing-account sign-in or new-account registration, verified-recipient token redemption, and atomic one-use family-member activation with explicit parent access for only the invited family/team in `backend/src/main/java/de/teammanagement/auth/InvitationService.java` and `InvitationController.java`; require already-active family and family-team membership, serialize with deactivation, reject inactive membership without consuming the token, and audit resulting membership/grant changes in the transaction. Acceptance must never create or reactivate family-team membership.
- [ ] T025 [US2] Implement transactional invitation outbox delivery, external encryption key configuration, retry/expiry handling, secure payload cleanup, and SMTP failure visibility in `backend/src/main/java/de/teammanagement/notifications/InvitationMailWorker.java` and `backend/src/main/resources/application.yaml`.
- [ ] T026 [US2] Build team-scoped and club-wide family/player management, explicit manager reactivation and parent-team access controls, invitation status/revocation, registration/acceptance with visible inactive-membership errors, and family/player import preview flows in `src/features/families/FamilyManagement.tsx`, `src/features/players/PlayerManagement.tsx`, and `src/features/auth/InvitationPage.tsx`.
- [ ] T027 [US2] Run the invitation and family isolation journey in `tests/e2e/family-invitations.spec.ts`, using the local mail catcher; shared task visibility is additionally verified by T039 once US4 exists.

**Checkpoint**: US2 can be verified with seeded team-manager and family-assignment fixtures; task-visibility integration is verified after US4.

## Phase 5: User Story 3 - Spielplan pflegen und importieren (Priority: P1)

**Goal**: Provide team-scoped match management and safe partial CSV/JSON imports.

**Independent Test**: Create/edit/remove matches, distinguish past and future matches, and preview/apply a file containing valid, invalid, and duplicate rows. Once US4 provides assignments, repeat the import and confirm existing assignments are unchanged.

### Tests for User Story 3

- [ ] T028 [US3] Define match and match-import operations and schemas first in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add match authorization, historical removal, CSV mapping, duplicate preview, mixed-validity import, revalidation, and OpenAPI response-conformance tests in `backend/src/test/java/de/teammanagement/matches/MatchImportIntegrationTest.java`; add the existing-assignment preservation case after T036 supplies its schema.
- [ ] T029 [P] [US3] Add match list/form/import preview tests in `src/features/matches/MatchManagement.test.tsx`.

### Implementation for User Story 3

- [ ] T030 [US3] Add match and one-per-match washing-task schema and mappings in `backend/src/main/resources/db/changelog/005-matches.yaml` and `backend/src/main/java/de/teammanagement/matches/Match.java`; implement [data-model.md](./data-model.md) fields `Match.id`, `Match.teamId`, `Match.matchDate`, `Match.matchTime`, `Match.opponent`, `Match.homeAway`, `Match.location`, `Match.note`, `Match.status`, `Match.createdAt`, `Match.updatedAt`, `WashingTask.id`, `WashingTask.matchId`, `WashingTask.status`, `WashingTask.createdAt`, and `WashingTask.completedAt`, including one washing task per created match.
- [ ] T031 [US3] Implement team-authorized match CRUD, chronological past/future lists, confirmation-aware removal preserving historical data, and match CSV/JSON preview/apply adapters with field mapping and duplicate warnings in `backend/src/main/java/de/teammanagement/matches/MatchService.java`, `MatchController.java`, and `backend/src/main/java/de/teammanagement/imports/MatchImport.java`.
- [ ] T032 [US3] Build schedule, match editing/removal confirmation, and CSV/JSON field-mapping/preview UI in `src/features/matches/MatchManagement.tsx` and `src/features/matches/MatchImport.tsx`.
- [ ] T033 [US3] Verify mixed imports and schedule/history flows in `tests/e2e/match-import.spec.ts`; after US4, rerun with existing task assignments and verify they are never silently overwritten.

**Checkpoint**: US3's schedule and import can be demonstrated with a seeded team; assignment-preservation regression requires US4's assignment schema and is a release gate.

## Phase 6: User Story 4 - Familienaufgaben übernehmen (Priority: P1)

**Goal**: Let parents claim/withdraw washing and catering duties and view dashboards and family tasks across teams.

**Independent Test**: With pre-seeded matches and catering posts, claim and withdraw duties as a parent on a smartphone, reject unauthorized or over-capacity claims including concurrent claims, and view family tasks with team context.

### Tests for User Story 4

- [ ] T034 [US4] Define dashboard, family-assignment, washing, and catering-claim operations and schemas first in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add PostgreSQL concurrent washing/catering claim, duplicate-family, deactivated-family, membership, cross-team authorization, and OpenAPI response-conformance tests in `backend/src/test/java/de/teammanagement/assignments/AssignmentIntegrationTest.java`.
- [ ] T035 [P] [US4] Add parent dashboard, family task list, claim/withdraw confirmation, capacity conflict, and mobile layout tests in `src/features/assignments/ParentTasks.test.tsx`.

### Implementation for User Story 4

- [ ] T036 [US4] Add washing-assignment and catering-post/assignment schemas and mappings in `backend/src/main/resources/db/changelog/006-assignments.yaml` and `backend/src/main/java/de/teammanagement/assignments/Assignment.java`; implement [data-model.md](./data-model.md) fields `WashingAssignment.id`, `WashingAssignment.washingTaskId`, `WashingAssignment.familyId`, `WashingAssignment.assignedAt`, `WashingAssignment.removedAt`, `WashingAssignment.status`, `CateringTask.id`, `CateringTask.matchId`, `CateringTask.name`, `CateringTask.category`, `CateringTask.requiredCount`, `CateringTask.createdAt`, `CateringAssignment.id`, `CateringAssignment.cateringTaskId`, `CateringAssignment.familyId`, `CateringAssignment.assignedAt`, `CateringAssignment.removedAt`, and `CateringAssignment.status`, with partial unique indexes for active washing and duplicate family catering assignments.
- [ ] T037 [US4] Implement transactional claim/withdraw and completion, explicit per-parent/family/team authorization, catering row locking across every capacity-changing path, status aggregation, dashboard, and family-task reads filtered by each parent's team grants in `backend/src/main/java/de/teammanagement/assignments/AssignmentService.java`, `AssignmentController.java`, and `DashboardController.java`; use [data-model.md](./data-model.md) `ParentTeamAccess.status` and never expose another team's current or historical assignments through the global family endpoint.
- [ ] T038 [US4] Build mobile-first dashboard, match/washing/catering lists, own-task history, team labels, accessible status text, confirmations, and conflict refresh in `src/features/assignments/ParentTasks.tsx` and `src/features/assignments/Dashboard.tsx`.
- [ ] T039 [US4] Verify a two-parent shared family, multi-team task visibility filtered by explicit parent grants, claim/withdraw, forbidden team access even when the family belongs to that team, and concurrent last-place outcomes in `tests/e2e/parent-assignments.spec.ts` and `backend/src/test/java/de/teammanagement/assignments/AssignmentIntegrationTest.java`.

**Checkpoint**: US4 works with pre-seeded catering posts; US5 adds organizer post management.

## Phase 7: User Story 5 - Wasch- und Bewirtungsaufgaben organisieren (Priority: P1)

**Goal**: Let authorized team managers and parent coordinators organize tasks and manage home-game catering.

**Independent Test**: Appoint a coordinator, create and copy a home-game catering list without assignments, correct washing/catering assignments, and verify forbidden administrative operations and away-match restrictions.

### Tests for User Story 5

- [ ] T040 [US5] Define coordinator appointment, catering post management/copy/import, and assignment correction operations and schemas first in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add coordinator-limit and authorization, home/away, capacity-edit, correction, copy-independence, concurrent appointment, and OpenAPI response-conformance tests in `backend/src/test/java/de/teammanagement/assignments/TaskOrganizationIntegrationTest.java`.
- [ ] T041 [P] [US5] Add coordinator organization, list copy, and inaccessible administrative actions tests in `src/features/assignments/TaskOrganization.test.tsx`.

### Implementation for User Story 5

- [ ] T042 [US5] Implement team-manager appointment/revocation of one active coordinator with audit and concurrency protection in `backend/src/main/java/de/teammanagement/teams/CoordinatorService.java` and `CoordinatorController.java`; enforce [data-model.md](./data-model.md) `TeamRoleAssignment.role`, `TeamRoleAssignment.active`, and `AuditEvent.details`. Require active parent eligibility with family-team membership and explicit parent access in that team at appointment and on every coordinator operation; add rejection tests for manager-only users, foreign-team parents, and lost parent eligibility in `backend/src/test/java/de/teammanagement/assignments/TaskOrganizationIntegrationTest.java`.
- [ ] T043 [US5] Implement home-only catering activation, post CRUD and copy-as-empty, safe capacity changes, washing/catering correction and completion in `backend/src/main/java/de/teammanagement/assignments/TaskOrganizationService.java` and `TaskOrganizationController.java`; reuse US4's lock and authorization rules and [data-model.md](./data-model.md) `CateringTask.requiredCount` and `WashingTask.status`.
- [ ] T044 [US5] Add catering CSV/JSON import adapter and preview integration in `backend/src/main/java/de/teammanagement/imports/CateringImport.java`, reusing the authorization and capacity rules.
- [ ] T045 [US5] Build coordinator appointment, task organization, post editing/copy, assignment correction, and catering import preview in `src/features/assignments/TaskOrganization.tsx` and `src/features/teams/CoordinatorManagement.tsx`.
- [ ] T046 [US5] Verify copy independence, coordinator privileges, away-match behavior, and capacity correction in `tests/e2e/task-organization.spec.ts`.

**Checkpoint**: Organizers can manage tasks seeded or created through US3/US4 without changing roster or club permissions.

## Phase 8: User Story 6 - Mehrere Rollen und Mannschaften nutzen (Priority: P1)

**Goal**: Enable one account to switch authorized teams and role-specific workspaces without reauthentication.

**Independent Test**: Give one account manager and parent roles in one team and a parent membership in another; switch teams and workspaces, verify context labels and permissions, then revoke a role without changing family memberships or assignments.

### Tests for User Story 6

- [ ] T047 [US6] Extend session and team-listing schemas first in `specs/001-club-team-management/contracts/openapi.yaml` for the multi-context workflow, regenerate the client, then add overlapping-role, revoked-role, family-independence, cross-team isolation, and OpenAPI response-conformance tests in `backend/src/test/java/de/teammanagement/auth/MultiContextIntegrationTest.java`.
- [ ] T048 [P] [US6] Add active team/workspace switching and context-label tests in `src/app/TeamContext.test.tsx`.

### Implementation for User Story 6

- [ ] T049 [US6] Extend authenticated session/team listing to return explicitly authorized administrative, management, and parent contexts without coupling family membership to role changes in `backend/src/main/java/de/teammanagement/auth/SessionController.java` and `backend/src/main/java/de/teammanagement/teams/TeamContextService.java`; use [data-model.md](./data-model.md) `ClubRole.role`, `TeamRoleAssignment.role`, `FamilyMembership.status`, `FamilyMember.status`, and `ParentTeamAccess.status`; verify a family's additional team memberships never expand a parent's contexts in `backend/src/test/java/de/teammanagement/auth/MultiContextIntegrationTest.java`.
- [ ] T050 [US6] Implement active team selection, role-aware workspace switching, context labels, and safe state refresh without logout in `src/app/TeamContext.tsx` and `src/app/AppShell.tsx`.
- [ ] T051 [US6] Verify role overlap, context switching without reauthentication, revocation, and unauthorized-team denial in `tests/e2e/team-context.spec.ts`.

**Checkpoint**: US6 can be tested using seeded memberships and roles; task and management links integrate with US1, US2, and US4.

## Phase 9: User Story 7 - Saisonwechsel nachvollziehbar durchführen (Priority: P2)

**Goal**: Preview and atomically transfer players between teams/seasons while preserving history and existing duties; support manual season archival.

**Independent Test**: Preview a multi-player transfer, cancel without changes, then confirm and verify all-or-none behavior, historic assignments, no duty/role transfer, and archived season visibility. Use US4 to create duties for the no-transfer check.

### Tests for User Story 7

- [ ] T052 [US7] Define transfer preview/confirm, player history, and season management/archive operations and schemas first in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add PostgreSQL bulk-transfer rollback, concurrent active-membership, stale preview, identity/history, no duty/role transfer, season archival, and OpenAPI response-conformance tests in `backend/src/test/java/de/teammanagement/players/SeasonTransferIntegrationTest.java`.
- [ ] T053 [P] [US7] Add transfer preview, cancellation/confirmation, invalid selection, and season archive UI tests in `src/features/players/SeasonTransfer.test.tsx`.

### Implementation for User Story 7

- [ ] T054 [US7] Implement authorized player transfer preview and confirmation with stable ordered row locks, complete revalidation, atomic rollback, preserved prior membership, transactional actor/time/record audit, and separate destination-parent access review in `backend/src/main/java/de/teammanagement/players/SeasonTransferService.java` and `SeasonTransferController.java`; enforce [data-model.md](./data-model.md) `PlayerTeamAssignment.status` and the active-player uniqueness invariant. Never create destination parent-access grants automatically, and test that rollback leaves neither partial memberships nor successful-change audit events in `backend/src/test/java/de/teammanagement/players/SeasonTransferIntegrationTest.java`.
- [ ] T055 [US7] Implement season creation/management, explicit archival and historical read views, and optional family/player carry-forward with role/invitation review in `backend/src/main/java/de/teammanagement/club/SeasonService.java` and `SeasonController.java`; use [data-model.md](./data-model.md) `Season.status` and preserve match/assignment history. Make archived matches and duties read-only for every role, rejecting claim, withdraw, completion, correction, edit, and import writes using the same transactional lock protocol as archival; exclude even incomplete archived duties from current dashboards/lists while retaining authorized history. Add backend and UI regression cases in `backend/src/test/java/de/teammanagement/players/SeasonTransferIntegrationTest.java` and `src/features/assignments/ParentTasks.test.tsx`.
- [ ] T056 [US7] Build season overview, roster source/target preview with names/counts/seasons, cancel/confirm controls, transfer errors, and player history in `src/features/players/SeasonTransfer.tsx` and `src/features/administration/SeasonManagement.tsx`.
- [ ] T057 [US7] Verify cancel, atomic confirmation, previous-team history, unchanged family duties, and manual archive views in `tests/e2e/season-transfer.spec.ts`.

**Checkpoint**: US7 can be demonstrated with seeded players and teams and US4 task records; no roles or duties are carried into the destination.

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Validate requirements that span stories and document actual operational setup.

- [ ] T058 [P] Verify keyboard navigation, textual task states, responsive parent and administration views, and no horizontal scrolling in central parent workflows in `tests/e2e/responsive-accessibility.spec.ts`.
- [ ] T059 [P] Document actual local setup, seed/bootstrap, Liquibase changelog workflow, OpenAPI documentation and client generation, SMTP configuration, and test commands in `README.md`; keep `specs/001-club-team-management/quickstart.md` consistent with implemented commands.
- [ ] T060 Validate `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client twice and check reproducibility, compile the generated client with the frontend, run backend contract and PostgreSQL tests using `backend/mvnw.cmd verify`, frontend tests using `package.json` scripts, `npm run lint`, `npm run build`, and `tests/e2e/` browser journeys; resolve failures or record explicit blockers in `specs/001-club-team-management/quickstart.md`.
- [ ] T061 Conduct and record the spec's usability validation in `specs/001-club-team-management/quickstart.md`: at least 9 of 10 representative parents complete the assignment within 60 seconds and at least 9 of 10 test participants identify active team and status within 10 seconds; record method, counts, timing, and any unmet criterion without claiming simulated tests as user research.

## Dependencies & Execution Order

### Phase Dependencies

- **Setup → Foundation**: T001 precedes T004 and T005–T009; T002 precedes T010–T012; T003 precedes the local-infrastructure checkpoint. T005 precedes T006–T009. T006 defines the foundational contract before backend implementation; T010 depends on T006 and generates the client before frontend integration. T006–T010 precede T011–T012. T008 is needed for independent story fixtures.
- **US1 → US2 and US3**: Team and team-manager operations are required for real user flows. US2 and US3 may begin after US1; US3's assignment-preservation regression runs after US4 supplies assignments.
- **US2 + US3 → US4**: US4 needs family memberships, matches, and the per-match washing task. Seed catering posts until US5 is complete.
- **US4 → US5**: Coordinator management extends the shared assignment model, locking, and claim/correction rules.
- **US1 + US2 → US6**: Session membership switching depends on roles and family memberships; for the full parent-task navigation checkpoint also integrate US4.
- **US1 + US2 + US3 → US7**: The season workflow needs team/player history and match archival. The full US7 checkpoint also needs US4 to create duties and prove they are not transferred.
- **Polish**: T058–T059 may run independently after the relevant UI and workflow files exist; T060 follows all implementation tasks; T061 requires a functioning parent workflow and recruited representative users.

### Within Each User Story

- Write and observe the specified tests fail for the intended behavior before implementing it.
- Complete the story's OpenAPI definition and regenerate the client before its other tests and implementation; all frontend API integrations use generated methods and models via the shared transport adapter. Do not edit generated files.
- Apply each story's Liquibase changeset and model before its service, endpoint, and UI integration.
- Independent tests can use explicitly scoped fixtures; final end-to-end checks use the real upstream story functionality.
- Tasks touching shared files, including `contracts/openapi.yaml`, generated client output, `application.yaml`, `AppShell.tsx`, the master changelog, and shared assignment services, must be serialized even if different stories are otherwise parallel.

### Parallel Opportunities

- After T001 starts independently, T002 and T003 can run concurrently; T004 waits for T001.
- After each story's OpenAPI task and regeneration, its remaining test files marked `[P]` can run concurrently where they do not depend on unfinished work.
- Once US1 is complete, US2 and US3 can be implemented in parallel with separate changelogs; serialize OpenAPI edits and client regeneration, and serialize registration in `backend/src/main/resources/db/changelog/db.changelog-master.yaml`.
- US6's session/context work and US7's transfer work can proceed after their listed prerequisites; coordinate shared team and UI files and defer cross-story integration until its dependencies exist.

## Parallel Examples per Story

- **US1**: After T013 updates and generates the contract, T014 can proceed independently of implementation in T015.
- **US2**: After T018 updates and generates the contract, T019 and T020 test distinct invitation and UI files in parallel.
- **US3**: After T028 updates and generates the contract, T029 can proceed independently of model implementation in T030.
- **US4**: After T034 updates and generates the contract, T035 can proceed independently of model implementation in T036.
- **US5**: After T040 updates and generates the contract, T041 can proceed independently of backend implementation in T042.
- **US6**: After T047 updates and generates the contract, T048 can proceed independently of backend implementation in T049.
- **US7**: After T052 updates and generates the contract, T053 can proceed independently of backend implementation in T054.

## Implementation Strategy

### MVP First

1. Complete setup and foundation, including secure test-only bootstrap and Liquibase migrations.
2. Complete US1 and its independent checkpoint to demonstrate club/team administration.
3. This is the first demonstrable increment, **not the full SRS MVP**: complete US2–US6 and the SRS-required player-transfer capabilities in US7 before claiming the complete first release.

### Incremental Delivery

1. Deliver US1, then US2 and US3; prove invitations, family/player boundaries, and schedules with their checkpoints.
2. Deliver US4 then US5; verify capacity races, permissions, and organizer workflows against PostgreSQL.
3. Deliver US6 and US7 after their dependencies; verify multiple contexts, player history, and atomic transfers.
4. Run all cross-cutting quality gates and the representative usability study before release.

### Notes

- IDs are sequential; `[P]` is limited to explicit independent waves and is not a license to edit shared files concurrently.
- The plan selects Liquibase, not Flyway; add each changeset to the master changelog in dependency order.
- Keep the OpenAPI contract authoritative; regenerate the ignored TypeScript client with the pinned generator after contract changes and before frontend development, tests, and builds.
- The product spec leaves the exact invitation expiry duration and import column schemas to implementation configuration/design. Set and document them without bypassing validation or inventing new user-facing scope.
- Do not mark the user-study target met without actual participant results.
