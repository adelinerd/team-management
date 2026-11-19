# Data Model: Vereinsweites Mannschaftsmanagement

**Date**: 2026-11-19
**Spec**: [spec.md](./spec.md)
**Research**: [research.md](./research.md)

## Conventions

- IDs are stable opaque identifiers. Every organization-owned record belongs to
  the single configured club, directly or through its parent record.
- Persist instants in UTC. Match dates and times are interpreted in the club's
  configured time zone; use `Europe/Berlin` as the initial default.
- Keep personal data to the fields required by the SRS. Do not collect player
  birth dates or additional contact details without an approved requirement.
- Historical records are deactivated or archived rather than silently removed.
  Active-state uniqueness constraints apply only to current memberships and
  assignments.

## Entities

### Club

Represents the organization in the first deployment.

- `id`: stable identifier
- `name`: required display name
- `externalKey`: required unique club key
- `timeZone`: required time-zone identifier
- `createdAt`, `updatedAt`

### Season

Represents a club season.

- `id`, `clubId`
- `name`: required label, such as `2026/2027`
- `startDate`, `endDate`
- `status`: planned, active, or archived

**Relationships**: A club has seasons. A season belongs to exactly one club.

### Team

Represents a team for a specific season.

- `id`, `clubId`, `seasonId`
- `name`: required
- `description`: optional
- `status`: active or inactive
- `createdAt`, `updatedAt`

**Relationships**: A team belongs to one club and one season. Team-specific
matches, roles, family memberships, and player assignments reference that team.

### UserAccount and ClubRole

`UserAccount` is the application's authenticated identity and minimal profile.
Password verification uses Spring Security's adaptive password encoder; only
the resulting one-way password hash is persisted and it is never exposed to
the UI.

- `id`: stable identity shared across all teams
- `email`: verified, unique sign-in address
- `passwordHash`: adaptive one-way hash
- `displayName`: optional until account setup is complete
- `status`: active or disabled
- `createdAt`

`ClubRole` associates a user with a club-wide role.

- `clubId`, `userId`
- `role`: `CLUB_ADMIN`
- `createdAt`, `revokedAt`

### TeamRoleAssignment

Represents a team-scoped management role. Family membership is modeled
separately.

- `id`, `teamId`, `userId`
- `role`: `TEAM_MANAGER` or `PARENT_COORDINATOR`
- `managerSlot`: 1–3 for team managers; null for a coordinator
- `active`, `appointedBy`, `createdAt`, `revokedAt`

**Relationships**: A user may have multiple roles on a team and roles on
multiple teams. A parent coordinator must remain an active parent with an
active family-team membership and explicit parent access for that team.

### Family and FamilyMembership

`Family` represents the unit that accepts washing and catering duties.

- `id`, `clubId`
- `familyName`: required
- `contactName`, `contactEmail`: optional
- `status`: active or inactive

`FamilyMembership` connects a family to a team.

- `id`, `familyId`, `teamId`
- `status`: active or inactive
- `createdAt`, `deactivatedAt`

`FamilyMember` connects one or more user accounts to a family.

- `familyId`, `userId`
- `status`: active or inactive
- `createdAt`, `removedAt`

**Relationships**: A family may belong to multiple teams and have multiple
parent accounts. A parent account may belong to multiple families. Parents see
the same family assignments only for teams each parent is explicitly authorized
to access; family membership alone does not grant team access.

### ParentTeamAccess

An explicit parent grant for one family and team, separate from management roles.

- `id`: stable identifier
- `userId`, `familyId`, `teamId`: required references; unique as a combination
- `status`: active or revoked
- `grantedAt`: required UTC instant
- `revokedAt`: nullable UTC instant, set on revocation

Access requires an active account, `FamilyMember`, `Family`, `FamilyMembership`,
and `ParentTeamAccess` for the requested family and team. A grant for one team
never grants another team's data. Family-task reads, including history, filter
each result by this grant. Club-wide family administration is a separate
administrator permission and never substitutes for parent assignment rights.

### Player and PlayerTeamAssignment

`Player` is the stable, club-wide player identity.

- `id`, `clubId`, `familyId`
- `name`: required
- `status`: active or inactive
- `createdAt`, `updatedAt`

`PlayerTeamAssignment` records a season-specific roster assignment.

- `id`, `playerId`, `teamId`, `seasonId`
- `startDate`, `endDate`
- `status`: active or ended
- `createdAt`

**Relationships**: A player belongs to one family and has zero or more
historical team assignments. There may be at most one active assignment for a
player at a time.

### Invitation

Represents an invitation to join a family in a team.

- `id`, `clubId`, `teamId`, `familyId`
- `invitedEmail`: normalized target email
- `invitedBy`: user identity
- `tokenHash`: hash of a random one-time bearer token; never store the raw token
- `status`: pending, accepted, expired, or revoked
- `createdAt`, `expiresAt`, `acceptedAt`, `revokedAt`

**Relationships**: Acceptance creates or activates a `FamilyMember` and grants
`ParentTeamAccess` for only the invited family and team in one transaction.
The family and its team membership must already be active. A team manager must
explicitly reactivate an inactive family-team membership first; acceptance
neither creates nor reactivates that membership. The token must be unused,
unexpired, unrevoked, and bound to the target email.

### Match

Represents a team fixture.

- `id`, `teamId`
- `matchDate`, `matchTime`
- `opponent`
- `homeAway`: home or away
- `location`, `note`: optional
- `status`: scheduled, cancelled, or removed
- `createdAt`, `updatedAt`

Past scheduled matches remain viewable. Removing a match from the active
schedule preserves its task and assignment history.

### WashingTask and WashingAssignment

`WashingTask` is the single washing duty for a match.

- `id`, `matchId`: unique
- `status`: open, assigned, or completed
- `createdAt`, `completedAt`

`WashingAssignment` records family ownership.

- `id`, `washingTaskId`, `familyId`
- `assignedAt`, `removedAt`
- `status`: active or withdrawn

At most one active family may be assigned to a washing task. A family may only
accept tasks for a team where its family membership is active.

### CateringTask and CateringAssignment

`CateringTask` is one contribution or helper duty at a home match.

- `id`, `matchId`
- `name`: required
- `category`: contribution or service
- `requiredCount`: positive integer
- `createdAt`

`CateringAssignment` connects a family to one slot.

- `id`, `cateringTaskId`, `familyId`
- `assignedAt`, `removedAt`
- `status`: active or withdrawn

The same family may occupy a given post at most once. Active assignments must
not exceed `requiredCount`.

### AuditEvent

Captures required administrative history, including appointment and revocation
of team managers and parent coordinators.

- `id`, `clubId`, `actorUserId`
- `eventType`, `entityType`, `entityId`
- `occurredAt`
- `details`: minimal non-secret context required to understand the change

Never include invitation tokens, passwords, or unnecessary personal data in
audit details.

### ImportBatch

Short-lived server-side state for previewing and applying a file import.

- `id`, `teamId`, `createdBy`
- `importType`: matches, catering, families, or players
- `status`: previewed, applied, expired, or cancelled
- `expiresAt`, `appliedAt`
- `stagedRows`: normalized candidate data retained only until apply or expiry
- row results: source row number, validation state, safe error details, and
  whether the row was applied

Retain staged rows only for the short preview window; remove the source file
and staged personal data when the batch is applied or expires. The creator and
team scope every read of a preview. Each row is independently validated so
invalid rows do not suppress valid rows.

### EmailOutboxItem

Reliable delivery record for invitation messages.

- `id`, `invitationId`
- `encryptedPayload`: short-lived encrypted delivery data containing the
  recipient and one-time link; decryptable only by the mail worker
- `status`: pending, sent, or failed
- `attemptCount`, `nextAttemptAt`, `sentAt`
- `lastErrorCode`: safe operational code only

The message is inserted with the invitation transaction. Delete the encrypted
payload after successful delivery; retries must not recreate or re-enable a
revoked or expired invitation. Never log the payload or link.

## Relationships

```text
Club 1 ── * Season 1 ── * Team
Club 1 ── * ClubRole ── 1 UserAccount
Team 1 ── * TeamRoleAssignment ── 1 UserAccount
Family 1 ── * FamilyMembership ── 1 Team
Family 1 ── * FamilyMember ── 1 UserAccount
UserAccount 1 ── * ParentTeamAccess ── 1 FamilyMembership (familyId, teamId)
Family 1 ── * Player
Player 1 ── * PlayerTeamAssignment ── 1 Team
Team 1 ── * Match
Match 1 ── 1 WashingTask 1 ── * WashingAssignment ── 1 Family
Match 1 ── * CateringTask 1 ── * CateringAssignment ── 1 Family
FamilyMembership 1 ── * Invitation
Invitation 1 ── * EmailOutboxItem
Club 1 ── * AuditEvent
Team 1 ── * ImportBatch
```

## Validation and Integrity Rules

1. A team, family, player, invitation, or match mutation must be scoped to the
   authenticated user's club and the applicable team role. Client-supplied IDs
   are selectors, never proof of permission.
2. A team has at most three active `TEAM_MANAGER` assignments. Reserve one of
   three unique manager slots while holding the team row lock. A team has at
   most one active `PARENT_COORDINATOR`.
3. Enforce at most one active `PlayerTeamAssignment` per player with a
   PostgreSQL partial unique index. Team and season must belong to the same club.
4. A match has at most one washing task. A washing task has at most one active
   washing assignment. Family and match must belong to the same team.
5. Catering capacity must be checked while holding a lock on the
   `CateringTask`; reject the write if the active assignment count would exceed
   `requiredCount`. Enforce one active assignment per `(cateringTaskId,
   familyId)`.
6. A roster transfer validates all selected players and destination teams,
   closes the prior active assignment, and creates the new assignment in one
   transaction. Any invalid or conflicting row rolls back the entire transfer.
7. An import preview is bound to its creator and team. On apply, revalidate
   against current state; apply valid rows only, report each rejected row, and
   never replace existing washing or catering assignments silently.
8. Invitation acceptance checks token hash, expiry, revocation, and matching
   verified email atomically with family-member activation and the explicit
   parent-team grant. It rechecks active family-team membership under the same
   transaction/locking protocol as deactivation. A token can be consumed only once.
9. Deactivation and archival preserve historical assignments and audit events;
   they prevent new assignments without rewriting old ones.
10. Every parent read and assignment mutation checks explicit parent-team access,
    not just family affiliation. Season transfers never create these grants.
11. Administrative family, family-member, parent-access, and player-assignment
    mutations, including imports and transfers, record actor, UTC time, and
    affected record in the same transaction. Rolled-back changes leave no
    successful-change audit event.
12. Archived matches and duties allow authorized historical reads only.
    All match and duty mutations, including corrections and imports, reject archived
    targets; incomplete duties are excluded from current-task views.
13. Coordinator authority requires current active parent eligibility on every
    operation, not only appointment. Losing family membership or parent-team
    access must not leave effective coordinator privileges.

## State Transitions

| Entity | Allowed transitions |
|---|---|
| Team | active → inactive |
| Season | planned → active → archived |
| Team role assignment | active → revoked |
| Family membership | active → inactive; inactive → active only by explicit team-manager action |
| Parent team access | active → revoked; a valid invitation may explicitly grant access again |
| Invitation | pending → accepted, expired, or revoked (terminal) |
| Player team assignment | active → ended |
| Match | scheduled → cancelled or removed |
| Washing task | open → assigned → completed; assigned → open when withdrawn |
| Catering assignment | active → withdrawn |
| Import batch | previewed → applied, expired, or cancelled |
| Email outbox item | pending → sent or retryable failed |
