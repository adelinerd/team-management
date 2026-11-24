---
description: "Implementation tasks for club-wide team management"
---

# Tasks: Vereinsweites Mannschaftsmanagement

**Input**: [spec.md](./spec.md), [plan.md](./plan.md), [research.md](./research.md), [data-model.md](./data-model.md), [API contract](./contracts/api-contract.md), [quickstart.md](./quickstart.md), and project constitution.

**Delivery approach**: Deliver a functional club-administration workspace first, then release the washing workflow as a small customer-demo increment, followed by catering. Each increment has a demo checkpoint; later capabilities do not block showing an earlier increment.

**Tests**: The spec requires acceptance checks, including concurrency and user research; the constitution requires tests for changed behavior and server-side authorization and invariant checks. Add focused tests for each increment and do not claim simulated tests as user research.

**Organization**: Tasks are grouped by the seven stories in [spec.md](./spec.md). The US4 phase is split into sequential washing and catering increments. Paths are relative to the repository root; planned files need not exist yet. Constrained field citations refer to the named `Entity.field` in [data-model.md](./data-model.md). `specs/001-club-team-management/contracts/openapi.yaml` is the canonical API-first specification; frontend API calls use its generated TypeScript client.

## Format: `[ID] [P?] [Story] Description`

- `[P]` marks tasks that can run concurrently in their dependency wave without editing the same files.
- Story tags map to the numbered stories in [spec.md](./spec.md). Setup, foundation, and polish tasks are unlabeled.
- Every task names the file or files it produces, changes, or validates.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Establish frontend/backend builds and local test infrastructure.

- [X] T001 Create the Java 21 Spring Boot 4.1.2 Maven project and wrapper in `backend/pom.xml`, `backend/mvnw`, `backend/mvnw.cmd`, and `backend/src/main/java/de/teammanagement/TeamManagementApplication.java`, including web, validation, security, JPA, Liquibase, PostgreSQL, mail, Spring test, and Testcontainers dependencies.
- [X] T002 [P] Add Vitest, React Testing Library, Playwright, and `test:run` and `test:e2e` scripts to `package.json` and `package-lock.json`; configure `vite.config.ts` and `playwright.config.ts` for component and browser tests.
- [X] T003 [P] Define local PostgreSQL 18 and mail-catcher services in `compose.yaml` and non-secret local configuration in `backend/src/main/resources/application-local.yaml`; use externally supplied credentials and no production defaults.
- [X] T004 Configure PostgreSQL, Liquibase startup, Hibernate schema validation, session cookie and SMTP timeout settings in `backend/src/main/resources/application.yaml`; create `backend/src/main/resources/db/changelog/db.changelog-master.yaml` and a test-only profile in `backend/src/test/resources/application.yaml`.

**Checkpoint**: Maven and npm tooling are configured; local services can be started without production credentials.

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Complete only shared security, persistence, API-client, and shell capabilities needed before the first demonstrable product.

- [X] T005 Add Liquibase changesets and JPA mappings for club, season, user, club role, team, team role, and audit records in `backend/src/main/resources/db/changelog/001-foundation.yaml` and `backend/src/main/java/de/teammanagement/common/`; implement [data-model.md](./data-model.md) fields `Club.name`, `Club.externalKey`, `Club.timeZone`, `Season.name`, `Season.status`, `Team.name`, `Team.description`, `Team.status`, `UserAccount.email`, `UserAccount.passwordHash`, `UserAccount.displayName`, `UserAccount.status`, `ClubRole.role`, `TeamRoleAssignment.role`, `TeamRoleAssignment.managerSlot`, and `AuditEvent.details` with canonical constraints and relationships. Also implement identity, ownership, and UTC timing per [data-model.md](./data-model.md): `Club.id`, `Club.createdAt`, `Club.updatedAt`, `Season.id`, `Season.clubId`, `Season.startDate`, `Season.endDate`, `Team.id`, `Team.clubId`, `Team.seasonId`, `Team.createdAt`, `Team.updatedAt`, `UserAccount.id`, `UserAccount.createdAt`, `ClubRole.clubId`, `ClubRole.userId`, `ClubRole.createdAt`, `ClubRole.revokedAt`, `TeamRoleAssignment.id`, `TeamRoleAssignment.teamId`, `TeamRoleAssignment.userId`, `TeamRoleAssignment.active`, `TeamRoleAssignment.appointedBy`, `TeamRoleAssignment.createdAt`, `TeamRoleAssignment.revokedAt`, `AuditEvent.id`, `AuditEvent.clubId`, `AuditEvent.actorUserId`, `AuditEvent.eventType`, `AuditEvent.entityType`, `AuditEvent.entityId`, and `AuditEvent.occurredAt`.
- [X] T006 Create `specs/001-club-team-management/contracts/openapi.yaml` with OpenAPI 3.0 definitions for foundational session, CSRF, errors, authenticated documentation endpoint, and shared security schemes before implementing Spring Security session authentication, adaptive password hashing, CSRF token endpoint and rotation, logout, session fixation protection, and explicit one-time admin bootstrap without committed credentials in `backend/src/main/java/de/teammanagement/auth/SecurityConfig.java`, `SessionController.java`, and `AdminBootstrap.java`.
- [X] T007 Implement deny-by-default club/team/family authorization helpers, team-scoped query rules, generic errors with safe correlation IDs, and secret-safe audit recording in `backend/src/main/java/de/teammanagement/common/AccessPolicy.java`, `ApiExceptionHandler.java`, and `AuditService.java`; family-dependent checks must resolve current membership rather than trust client IDs.
- [X] T008 Create local-only bootstrap fixtures and a PostgreSQL Testcontainers base in `backend/src/main/java/de/teammanagement/common/LocalSeed.java` and `backend/src/test/java/de/teammanagement/PostgresIntegrationTest.java`; apply the production Liquibase changelog and keep seeded accounts and secrets out of production.
- [X] T009 Configure pinned OpenAPI Generator CLI and `typescript-fetch` in `package.json`, `package-lock.json`, and `openapitools.json`, with `validate:api` and `generate:api` scripts using `specs/001-club-team-management/contracts/openapi.yaml` to generate into `src/generated/openapi/` (exclude generated files in `.gitignore` and `eslint.config.js`). Package the same source contract via `backend/pom.xml` and serve it to authenticated users at `GET /api/v1/openapi.yaml` in `backend/src/main/java/de/teammanagement/common/OpenApiDocumentationController.java`. Implement the generated-client transport adapter for same-origin credentials, CSRF token/header acquisition and renewal after session changes, safe error handling without automatic mutation replay, and Vite `/api/v1` proxy in `src/lib/api/client.ts` and `vite.config.ts`.
- [X] T010 Build the responsive application shell, login form, routes, basic role-aware workspace navigation and keyboard-accessible shared controls in `src/app/AppShell.tsx`, `src/features/auth/LoginPage.tsx`, `src/components/`, and `src/App.tsx`; use `ui-mocks/` as visual guidance.
- [ ] T011 Add foundational authorization, CSRF/login/logout, Liquibase startup, authenticated access to the packaged documentation, and generated-client session/CSRF/error tests in `backend/src/test/java/de/teammanagement/common/FoundationIntegrationTest.java`, `src/lib/api/client.test.ts`, and `src/features/auth/LoginPage.test.tsx`; validate actual backend status codes and bodies against `specs/001-club-team-management/contracts/openapi.yaml`.

**Checkpoint**: A fresh database migrates, the seeded administrator can sign in, and the shell accesses the API. Import infrastructure is deliberately not a prerequisite for the first customer demo.

## Phase 3: User Story 1 - Verein und Mannschaften verwalten (Priority: P1)

**Goal**: Deliver the first customer-showable version: a functional administrator workspace for club profile, teams, seasons, and manager roles.

**Independent Test**: Sign in as a club administrator, edit the club profile, create/edit/deactivate a team, appoint three managers and reject a fourth; confirm unauthorized calls are denied and actor/time audit records are present.

- [ ] T012 [US1] Define team, club-profile read/edit, season-listing, and manager-appointment operations and schemas first in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add PostgreSQL-backed name/key/time-zone validation, authorization, concurrent manager-limit, audit, and OpenAPI response-conformance tests in `backend/src/test/java/de/teammanagement/teams/TeamAdministrationIntegrationTest.java`.
- [ ] T013 [P] [US1] Add club administration form and access-denial tests in `src/features/administration/TeamAdministration.test.tsx`.
- [ ] T014 [US1] Implement club/team/season listing, club profile read/edit after one-time bootstrap, team create/edit/deactivate, and bounded manager appointment/revocation in `backend/src/main/java/de/teammanagement/teams/TeamAdministrationService.java`, `TeamAdministrationController.java`, and `backend/src/main/java/de/teammanagement/club/ClubController.java`; use [data-model.md](./data-model.md) `Club.name`, `Club.externalKey`, `Club.timeZone`, `TeamRoleAssignment.role`, `TeamRoleAssignment.managerSlot`, `TeamRoleAssignment.active`, `Team.status`, and `AuditEvent.details`, and serialize concurrent appointments.
- [ ] T015 [US1] Implement club overview and editable name/key/time-zone profile, team form, status controls, manager selection, and visible errors in `src/features/administration/TeamAdministration.tsx` and `src/features/administration/teamApi.ts`.
- [ ] T016 [US1] Exercise the US1 independent test and audit/denial checkpoint using local seeded users in `tests/e2e/club-administration.spec.ts`.

**Customer demo 1**: Stop after T016. Demonstrate club/team administration with local fixtures; do not wait for family, schedule, washing, or catering features.

## Phase 4: User Story 2 - Spieler, Familien und Einladungen verwalten (Priority: P1)

**Goal**: Provide the family, player, and parent access needed for real washing and catering workflows.

**Independent Test**: Create a family with multiple players, invite two parents and accept both invitations; verify shared family access, team isolation, inactive-membership behavior, and visible invitation errors.

- [ ] T017 [US2] Define family, player, invitation, parent-team access, explicit manager reactivation, and family/player operations and schemas first in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add team-scoped and club-wide family/player CRUD tests (including unassigned club records), active-assignment uniqueness, isolation, deactivation/reactivation/history, membership audit, and OpenAPI conformance in `backend/src/test/java/de/teammanagement/families/FamilyPlayerIntegrationTest.java`.
- [ ] T018 [P] [US2] Add invitation registration/acceptance, recipient verification, expiry, revocation, replay, concurrent acceptance, inactive family-team rejection without token consumption, concurrent deactivation/acceptance, successful acceptance after explicit manager reactivation, and OpenAPI conformance tests in `backend/src/test/java/de/teammanagement/auth/InvitationIntegrationTest.java`; prove an invitation to Team A grants no access to Team B.
- [ ] T019 [P] [US2] Add family management and invitation UI tests in `src/features/families/FamilyManagement.test.tsx`.
- [ ] T020 [US2] Add family/member, player, and player-team-assignment Liquibase changesets and mappings in `backend/src/main/resources/db/changelog/003-families-players.yaml`, `backend/src/main/java/de/teammanagement/families/Family.java`, and `backend/src/main/java/de/teammanagement/players/Player.java`; implement [data-model.md](./data-model.md) fields `Family.id`, `Family.clubId`, `Family.familyName`, `Family.contactName`, `Family.contactEmail`, `Family.status`, `FamilyMembership.id`, `FamilyMembership.familyId`, `FamilyMembership.teamId`, `FamilyMembership.status`, `FamilyMembership.createdAt`, `FamilyMembership.deactivatedAt`, `FamilyMember.familyId`, `FamilyMember.userId`, `FamilyMember.status`, `FamilyMember.createdAt`, `FamilyMember.removedAt`, `Player.id`, `Player.clubId`, `Player.familyId`, `Player.name`, `Player.status`, `Player.createdAt`, `Player.updatedAt`, `PlayerTeamAssignment.id`, `PlayerTeamAssignment.playerId`, `PlayerTeamAssignment.teamId`, `PlayerTeamAssignment.seasonId`, `PlayerTeamAssignment.startDate`, `PlayerTeamAssignment.endDate`, `PlayerTeamAssignment.status`, and `PlayerTeamAssignment.createdAt`, including the active-player partial unique index and team/season relationships.
- [ ] T021 [US2] Add explicit parent-access schema/mapping in `backend/src/main/resources/db/changelog/003-families-players.yaml` and `backend/src/main/java/de/teammanagement/families/ParentTeamAccess.java` per [data-model.md](./data-model.md) `ParentTeamAccess.id`, `ParentTeamAccess.userId`, `ParentTeamAccess.familyId`, `ParentTeamAccess.teamId`, `ParentTeamAccess.status`, `ParentTeamAccess.grantedAt`, and `ParentTeamAccess.revokedAt`. Implement team-scoped and club-wide family/player CRUD, parent-access administration, family-team deactivation and explicit manager reactivation, and transactional actor/time/record audit in `backend/src/main/java/de/teammanagement/families/FamilyService.java` and `backend/src/main/java/de/teammanagement/players/PlayerService.java`; expose HTTP operations in `FamilyController.java` and `PlayerController.java`. Update `backend/src/main/java/de/teammanagement/common/AccessPolicy.java` to check explicit parent grants separately from administrative permissions.
- [ ] T022 [US2] Add invitation and encrypted email-outbox Liquibase changesets and mappings in `backend/src/main/resources/db/changelog/004-invitations.yaml` and `backend/src/main/java/de/teammanagement/auth/Invitation.java`; implement [data-model.md](./data-model.md) fields `Invitation.id`, `Invitation.clubId`, `Invitation.teamId`, `Invitation.familyId`, `Invitation.invitedEmail`, `Invitation.invitedBy`, `Invitation.tokenHash`, `Invitation.status`, `Invitation.createdAt`, `Invitation.expiresAt`, `Invitation.acceptedAt`, `Invitation.revokedAt`, `EmailOutboxItem.id`, `EmailOutboxItem.invitationId`, `EmailOutboxItem.encryptedPayload`, `EmailOutboxItem.status`, `EmailOutboxItem.attemptCount`, `EmailOutboxItem.nextAttemptAt`, `EmailOutboxItem.sentAt`, and `EmailOutboxItem.lastErrorCode`.
- [ ] T023 [US2] Implement invite/revoke/status, configurable expiry, existing-account sign-in or new-account registration, verified-recipient token redemption, and atomic one-use family-member activation with explicit parent access for only the invited family/team in `backend/src/main/java/de/teammanagement/auth/InvitationService.java` and `InvitationController.java`; require already-active family and family-team membership, serialize with deactivation, reject inactive membership without consuming the token, and audit resulting membership/grant changes in the transaction. Acceptance must never create or reactivate family-team membership.
- [ ] T024 [US2] Implement transactional invitation outbox delivery, external encryption key configuration, retry/expiry handling, secure payload cleanup, and SMTP failure visibility in `backend/src/main/java/de/teammanagement/notifications/InvitationMailWorker.java` and `backend/src/main/resources/application.yaml`.
- [ ] T025 [US2] Build team-scoped and club-wide family/player management, explicit manager reactivation and parent-team access controls, invitation status/revocation, registration/acceptance with visible inactive-membership errors, and family/player screens in `src/features/families/FamilyManagement.tsx`, `src/features/players/PlayerManagement.tsx`, and `src/features/auth/InvitationPage.tsx`.
- [ ] T026 [US2] Run the invitation and family isolation journey using the local mail catcher in `tests/e2e/family-invitations.spec.ts`; shared task visibility is additionally verified in the US4 family-task journey.

## Phase 5: User Story 3 - Spielplan pflegen und importieren (Priority: P1)

**Goal**: Provide team-scoped fixtures as the foundation for washing and catering schedules.

**Independent Test**: Create/edit/remove matches, distinguish past and future matches, and verify removed-match history is retained. File import is scheduled in the later import increment.

- [ ] T027 [US3] Define match and match-import operations and schemas in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add match authorization, historical removal, duplicate detection, and OpenAPI response-conformance tests in `backend/src/test/java/de/teammanagement/matches/MatchImportIntegrationTest.java`.
- [ ] T028 [P] [US3] Add match list, form, and history UI tests in `src/features/matches/MatchManagement.test.tsx`.
- [ ] T029 [US3] Add match and one-per-match washing-task schema and mappings in `backend/src/main/resources/db/changelog/005-matches.yaml` and `backend/src/main/java/de/teammanagement/matches/Match.java`; implement [data-model.md](./data-model.md) fields `Match.id`, `Match.teamId`, `Match.matchDate`, `Match.matchTime`, `Match.opponent`, `Match.homeAway`, `Match.location`, `Match.note`, `Match.status`, `Match.createdAt`, `Match.updatedAt`, `WashingTask.id`, `WashingTask.matchId`, `WashingTask.status`, `WashingTask.createdAt`, and `WashingTask.completedAt`, including one washing task per created match.
- [ ] T030 [US3] Implement team-authorized match CRUD, chronological past/future lists, confirmation-aware removal preserving historical data, and audit/error responses in `backend/src/main/java/de/teammanagement/matches/MatchService.java` and `MatchController.java`.
- [ ] T031 [US3] Build schedule, match editing/removal confirmation, and visible date/time/status states in `src/features/matches/MatchManagement.tsx`.
- [ ] T032 [US3] Verify schedule, authorization, removal confirmation, and history in `tests/e2e/match-management.spec.ts`.

**Checkpoint**: The manager can maintain a schedule. Its per-match washing tasks are ready for the next demo increment.

## Phase 6: User Story 4 - Familienaufgaben übernehmen (Priority: P1)

**Goal**: Let parents claim and withdraw family tasks. Deliver washing first, then add catering as a separate demo increment.

### Increment 1: Washing schedule

**Independent Test**: With a seeded match and authorized family, claim and withdraw its washing task on mobile; reject unauthorized access and ensure a concurrent second claim cannot occupy the single slot.

- [ ] T033 [US4] Define washing task, family-task, dashboard, claim/withdraw, and completion operations in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add PostgreSQL washing concurrency, duplicate-family, deactivated-family, parent-team authorization, archived-target, and OpenAPI conformance tests in `backend/src/test/java/de/teammanagement/assignments/WashingAssignmentIntegrationTest.java`.
- [ ] T034 [P] [US4] Add washing list, claim/withdraw confirmation, single-slot conflict, and mobile layout tests in `src/features/assignments/ParentTasks.test.tsx`.
- [ ] T035 [US4] Add washing-assignment schema and mappings in `backend/src/main/resources/db/changelog/006-washing-assignments.yaml` and `backend/src/main/java/de/teammanagement/assignments/WashingAssignment.java`; implement [data-model.md](./data-model.md) fields `WashingAssignment.id`, `WashingAssignment.washingTaskId`, `WashingAssignment.familyId`, `WashingAssignment.assignedAt`, `WashingAssignment.removedAt`, and `WashingAssignment.status`, with a partial unique index allowing at most one active family per washing task.
- [ ] T036 [US4] Implement transactional washing claim/withdraw/completion, explicit per-parent/family/team authorization, dashboard and family-task reads filtered by each parent's team grants in `backend/src/main/java/de/teammanagement/assignments/AssignmentService.java`, `AssignmentController.java`, and `DashboardController.java`; use [data-model.md](./data-model.md) `ParentTeamAccess.status`, and reject mutations on archived matches.
- [ ] T037 [US4] Build mobile-first dashboard, match/washing lists, own-task history, team labels, accessible status text, confirmations, and conflict refresh in `src/features/assignments/ParentTasks.tsx` and `src/features/assignments/Dashboard.tsx`.
- [ ] T038 [US4] Verify shared-family visibility, explicit multi-team grant filtering, claim/withdraw, forbidden team access, and concurrent single-slot outcomes in `tests/e2e/parent-washing.spec.ts` and `backend/src/test/java/de/teammanagement/assignments/WashingAssignmentIntegrationTest.java`.

**Customer demo 2**: Stop after T038. Demonstrate the jersey-washing schedule with real matches, invited family accounts, and parent self-service; catering is not a prerequisite.

### Increment 2: Catering assignments

**Independent Test**: With a seeded home match, claim and withdraw catering slots as a parent; verify capacity is never exceeded under concurrent requests and away matches offer no catering.

- [ ] T039 [US4] Extend `specs/001-club-team-management/contracts/openapi.yaml` with catering claim/withdraw and status operations, regenerate the client, and add PostgreSQL capacity-race, duplicate-family, deactivated-family, team-authorization, archived-target, and OpenAPI conformance tests in `backend/src/test/java/de/teammanagement/assignments/CateringAssignmentIntegrationTest.java`.
- [ ] T040 [US4] Add catering claim, withdraw, capacity conflict, home/away visibility, and mobile status tests in `src/features/assignments/ParentTasks.test.tsx`.
- [ ] T041 [US4] Add catering-task and catering-assignment schemas and mappings in `backend/src/main/resources/db/changelog/007-catering-assignments.yaml` and `backend/src/main/java/de/teammanagement/assignments/CateringAssignment.java`; implement [data-model.md](./data-model.md) fields `CateringTask.id`, `CateringTask.matchId`, `CateringTask.name`, `CateringTask.category`, `CateringTask.requiredCount`, `CateringTask.createdAt`, `CateringAssignment.id`, `CateringAssignment.cateringTaskId`, `CateringAssignment.familyId`, `CateringAssignment.assignedAt`, `CateringAssignment.removedAt`, and `CateringAssignment.status`, including unique active family/post assignment constraints.
- [ ] T042 [US4] Implement transactional catering claim/withdraw, home-match validation, task-row locking for every capacity-changing operation, and status aggregation in `backend/src/main/java/de/teammanagement/assignments/AssignmentService.java` and `AssignmentController.java`; enforce [data-model.md](./data-model.md) `CateringTask.requiredCount` and `CateringAssignment.status`.
- [ ] T043 [US4] Add catering lists, free/filled/full states, claim/withdraw confirmation, capacity-conflict refresh, and home/away behavior to `src/features/assignments/ParentTasks.tsx`.
- [ ] T044 [US4] Verify family catering claim/withdraw, home/away visibility, capacity conflicts, and concurrent last-place outcomes in `tests/e2e/parent-catering.spec.ts` and `backend/src/test/java/de/teammanagement/assignments/CateringAssignmentIntegrationTest.java`.

**Customer demo 3**: Stop after T044. Add parent catering sign-up to the already demonstrable washing release.

## Phase 7: User Story 5 - Wasch- und Bewirtungsaufgaben organisieren (Priority: P1)

**Goal**: Let team managers and eligible parent coordinators organize and correct washing/catering tasks without gaining roster-administration rights.

**Independent Test**: Appoint an eligible coordinator, manage washing and catering, copy a home-game catering list as empty, correct assignments within the team, and reject role or away-match violations.

- [ ] T045 [US5] Define coordinator appointment, washing correction/completion, catering post management/copy, and capacity-edit operations in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add coordinator eligibility/limit, washing correction, home/away, capacity-edit, copy-independence, and OpenAPI conformance tests in `backend/src/test/java/de/teammanagement/assignments/TaskOrganizationIntegrationTest.java`.
- [ ] T046 [P] [US5] Add coordinator appointment and task-organization access-control tests in `src/features/assignments/TaskOrganization.test.tsx`.
- [ ] T047 [US5] Implement team-manager appointment/revocation of one active coordinator with audit and concurrency protection in `backend/src/main/java/de/teammanagement/teams/CoordinatorService.java` and `CoordinatorController.java`; enforce [data-model.md](./data-model.md) `TeamRoleAssignment.role`, `TeamRoleAssignment.active`, and `AuditEvent.details`. Require active parent eligibility and explicit parent access in that team at appointment and on every coordinator operation.
- [ ] T048 [US5] Implement washing assignment correction and completion for authorized managers/coordinators in `backend/src/main/java/de/teammanagement/assignments/TaskOrganizationService.java` and `TaskOrganizationController.java`; reuse US4 authorization and archived-match rejection rules.
- [ ] T049 [US5] Build coordinator appointment, washing organization, correction controls, and visible denial states in `src/features/assignments/TaskOrganization.tsx` and `src/features/teams/CoordinatorManagement.tsx`.
- [ ] T050 [US5] Verify washing correction, completion, coordinator eligibility, away-match restrictions, and role denial in `tests/e2e/washing-organization.spec.ts`.
- [ ] T051 [US5] Add catering post editing, capacity changes, copy-as-empty behavior, and independent copied lists in `backend/src/main/java/de/teammanagement/assignments/TaskOrganizationService.java` and `TaskOrganizationController.java`; reuse US4 capacity locks and enforce [data-model.md](./data-model.md) `CateringTask.requiredCount`.
- [ ] T052 [US5] Build home-only catering activation, post CRUD/copy, assignment correction, and capacity feedback in `src/features/assignments/TaskOrganization.tsx`.
- [ ] T053 [US5] Verify coordinator privileges, away-match behavior, capacity correction, and copy independence in `tests/e2e/task-organization.spec.ts`.

## Phase 8: User Story 6 - Mehrere Rollen und Mannschaften nutzen (Priority: P1)

**Goal**: Enable one account to switch authorized teams and role-specific workspaces without reauthentication.

**Independent Test**: Give one account manager and parent roles in one team and a parent membership in another; switch contexts and verify labels, permissions, and revocation behavior.

- [ ] T054 [US6] Extend session and team-listing schemas in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add overlapping-role, revoked-role, family-independence, cross-team isolation, and OpenAPI conformance tests in `backend/src/test/java/de/teammanagement/auth/MultiContextIntegrationTest.java`.
- [ ] T055 [P] [US6] Add active team/workspace switching and context-label tests in `src/app/TeamContext.test.tsx`.
- [ ] T056 [US6] Extend authenticated session/team listing to return explicitly authorized administrative, management, and parent contexts without coupling family membership to role changes in `backend/src/main/java/de/teammanagement/auth/SessionController.java` and `backend/src/main/java/de/teammanagement/teams/TeamContextService.java`; use [data-model.md](./data-model.md) `ClubRole.role`, `TeamRoleAssignment.role`, `FamilyMembership.status`, `FamilyMember.status`, and `ParentTeamAccess.status`.
- [ ] T057 [US6] Implement active team selection, role-aware workspace switching, context labels, and safe state refresh without logout in `src/app/TeamContext.tsx` and `src/app/AppShell.tsx`.
- [ ] T058 [US6] Verify role overlap, context switching without reauthentication, revocation, and unauthorized-team denial in `tests/e2e/team-context.spec.ts`.

## Phase 9: User Story 7 - Saisonwechsel nachvollziehbar durchführen (Priority: P2)

**Goal**: Preview and atomically transfer players between teams/seasons while preserving history and existing duties; support manual season archival.

**Independent Test**: Preview a multi-player transfer, cancel without changes, then confirm and verify all-or-none behavior, historical assignments, no duty/role transfer, and archived season visibility.

- [ ] T059 [US7] Define transfer preview/confirm, player history, and season management/archive operations in `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client, then add PostgreSQL rollback, concurrent active-membership, stale preview, identity/history, no duty/role transfer, season archival, and OpenAPI conformance tests in `backend/src/test/java/de/teammanagement/players/SeasonTransferIntegrationTest.java`.
- [ ] T060 [P] [US7] Add transfer preview, cancellation/confirmation, invalid selection, and season archive UI tests in `src/features/players/SeasonTransfer.test.tsx`.
- [ ] T061 [US7] Implement authorized player transfer preview/confirmation with stable ordered row locks, complete revalidation, atomic rollback, preserved prior membership, transactional actor/time/record audit, and separate destination-parent access review in `backend/src/main/java/de/teammanagement/players/SeasonTransferService.java` and `SeasonTransferController.java`; enforce [data-model.md](./data-model.md) `PlayerTeamAssignment.status` and active-player uniqueness. Never create destination parent-access grants automatically.
- [ ] T062 [US7] Implement season creation/management, explicit archival and historical read views, and optional family/player carry-forward with role/invitation review in `backend/src/main/java/de/teammanagement/club/SeasonService.java` and `SeasonController.java`; use [data-model.md](./data-model.md) `Season.status` and preserve match/assignment history. Archived matches and duties are read-only for every role and excluded from current-task views.
- [ ] T063 [US7] Build season overview, roster source/target preview with names/counts/seasons, cancel/confirm controls, transfer errors, and player history in `src/features/players/SeasonTransfer.tsx` and `src/features/administration/SeasonManagement.tsx`.
- [ ] T064 [US7] Verify cancel, atomic confirmation, previous-team history, unchanged family duties, and manual archive views in `tests/e2e/season-transfer.spec.ts`.

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Complete optional imports, operational documentation, accessibility, and release validation after the customer-demo increments work.

- [ ] T065 [P] Verify keyboard navigation, textual task states, responsive parent and administration views, and no horizontal scrolling in central parent workflows in `tests/e2e/responsive-accessibility.spec.ts`.
- [ ] T066 Build the common short-lived CSV/JSON import staging, field mapping, preview/apply and per-row error framework in `backend/src/main/resources/db/changelog/008-imports.yaml` and `backend/src/main/java/de/teammanagement/imports/ImportBatchService.java`; implement [data-model.md](./data-model.md) fields `ImportBatch.id`, `ImportBatch.teamId`, `ImportBatch.createdBy`, `ImportBatch.importType`, `ImportBatch.status`, `ImportBatch.stagedRows`, `ImportBatch.expiresAt`, and `ImportBatch.appliedAt`, bind previews to creator/team, revalidate on apply, and purge staged personal data on apply or expiry.
- [ ] T067 Add family/player, match, and catering import adapters and preview/apply operations in `backend/src/main/java/de/teammanagement/imports/FamilyPlayerImport.java`, `MatchImport.java`, `CateringImport.java`, and `ImportController.java`; preserve existing assignments and provide per-row validation results.
- [ ] T068 Document actual local setup, seed/bootstrap, Liquibase changelog workflow, OpenAPI documentation and client generation, SMTP configuration, and test commands in `README.md`; keep `specs/001-club-team-management/quickstart.md` consistent with implemented commands.
- [ ] T069 Validate `specs/001-club-team-management/contracts/openapi.yaml`, regenerate the client twice and check reproducibility, compile the generated client with the frontend, run backend contract and PostgreSQL tests using `backend/mvnw.cmd verify`, frontend tests using `package.json` scripts, `npm run lint`, `npm run build`, and `tests/e2e/` browser journeys; resolve failures or record explicit blockers in `specs/001-club-team-management/quickstart.md`.
- [ ] T070 Conduct and record the spec's usability validation in `specs/001-club-team-management/quickstart.md`: at least 9 of 10 representative parents complete the assignment within 60 seconds and at least 9 of 10 test participants identify active team and status within 10 seconds; record method, counts, timing, and any unmet criterion without claiming simulated tests as user research.

## Dependencies & Execution Order

### Phase Dependencies

- **Setup -> Foundation**: T001 precedes T004-T011; T002/T003 may run independently after setup begins. T005 precedes T006-T008 and the domain stories. T006 defines the foundational contract before backend implementation; T009 generates the client before T010 shell integration; T010 precedes T011 shell tests.
- **Demo 1**: Complete T001-T016. The functional club/team administration workspace is independently demonstrable and does not depend on later family, schedule, washing, or catering features.
- **US1 -> US2 and US3**: Team and manager operations are required for real user flows. US2 and US3 may proceed independently after US1 if parallel capacity is available; serialize changes to the shared OpenAPI file and master Liquibase changelog.
- **US2 + US3 -> US4 washing**: The washing demo needs authorized families/parents and matches. T033-T038 are the first parent-facing task increment.
- **US4 washing -> US4 catering**: T039-T044 depend on the assignment foundation and complete washing slice; catering capacity is a separate release increment and must not block Customer demo 2.
- **US4 -> US5**: Coordinator and organizer controls extend the assignment model and use the same authorization/locking rules. T045-T050 address washing organization; T051-T053 add catering organization.
- **US1 + US2 -> US6**: Session context switching depends on roles and family memberships; full task navigation also integrates US4.
- **US1 + US2 + US3 -> US7**: Season transfer needs team/player history and match archival; duty preservation checks integrate US4.
- **Polish**: T065-T068 complete cross-cutting accessibility, imports, and documentation; T069 follows all desired implementations; T070 requires a functioning parent workflow and recruited representative users.

### Within Each User Story

- Define each story's OpenAPI operations before endpoint implementation and regenerate the client; do not edit generated files.
- Apply each story's Liquibase changeset and model before its service, endpoint, and UI integration.
- Run backend and UI tests against the specific behavior of each demo increment.
- Tasks touching shared files, including `contracts/openapi.yaml`, generated client output, `application.yaml`, `AppShell.tsx`, the master changelog, and shared assignment services, must be serialized.

### Parallel Opportunities

- After T001 starts, T002 and T003 can run concurrently; T004 waits for T001.
- In US1, T013 UI tests can run independently from backend work after T012's contract is ready.
- In US2, T018 invitation tests and T019 UI tests touch separate files after T017 defines the contract.
- In US3, T028 UI tests can proceed independently from match schema/service work after T027.
- In US4, T034 washing UI tests and T040 catering UI tests are separate test work; contract changes and shared `ParentTasks.tsx` implementation remain serialized.
- Once US1 is complete, US2 and US3 can proceed in parallel using separate domain files; coordinate OpenAPI edits and registration in the master changelog.
- In US6 and US7, UI tests can proceed independently from their backend work after the respective contract task.

## Implementation Strategy

### MVP First

1. Complete setup and blocking security/API/shell foundation.
2. Complete US1 and its independent checkpoint.
3. **Stop and demo Customer demo 1**: a functional club administrator can manage the club and teams.

### Incremental Customer Demos

1. Add family/player access and match schedules (US2 + US3).
2. Add washing assignment and parent self-service (US4 T033-T038); **demo the jersey-washing schedule before starting catering**.
3. Add parent catering sign-up (US4 T039-T044); demo catering as a separate increment.
4. Add coordinator/organizer controls (US5), multiple roles/teams (US6), and season transfer/archive (US7) as later releases.
5. Complete imports, accessibility, full validation, and usability research before declaring the complete first release.

### Notes

- IDs are sequential; `[P]` is limited to explicit independent waves and never permits simultaneous edits to shared files.
- Checkboxes describe implementation progress; do not mark a task complete based only on planned or simulated behavior.
- The plan selects Liquibase, not Flyway. Register each changeset in `backend/src/main/resources/db/changelog/db.changelog-master.yaml` in dependency order.
- Keep the OpenAPI contract authoritative and regenerate the ignored TypeScript client after contract changes.
- Set invitation expiry and import column schemas as documented configuration/design choices without bypassing validation or inventing new user-facing scope.
- Do not mark the user-study target met without actual participant results.
