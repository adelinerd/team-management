# API Contract: Vereinsweites Mannschaftsmanagement

**Date**: 2026-11-19
**Spec**: [spec.md](../spec.md)
**Data model**: [data-model.md](../data-model.md)

## Boundary

The React application calls a same-origin JSON REST API under `/api/v1`. The
API is the only application boundary to persistent data. No database credentials
or privileged administrative credentials are sent to the browser.

Authentication uses an HTTP session cookie. The cookie is `Secure`,
`HttpOnly`, and `SameSite`; state-changing requests include the CSRF token
provided by the API. The API derives the user identity from the authenticated
session. Team, family, and player IDs in requests identify resources but do not
grant access to them.

## Authorization Contract

| Actor | Allowed scope |
|---|---|
| Unauthenticated visitor | Sign-in, invitation acceptance, and public health status only |
| Club administrator | Club seasons, teams, cross-team player administration, and manager appointments |
| Team manager | Management operations for teams where the user has an active manager assignment |
| Parent coordinator | Washing and catering operations for the assigned team; no roster, family membership, or role administration |
| Parent | Read team schedules and manage only the assignments of an actively linked family |

Every read and write operation checks the current user's role and the resource's
team/club relationship. Family membership and team-management roles are
independent. Users with multiple roles may use each authorized workflow, but a
management role alone does not grant family assignment rights.

## Operations

All paths below are relative to `/api/v1`. They describe the required resource
and command contract; exact payload field names are to be kept aligned with
the data model and versioned API definition during implementation.

| Operation | Method and path | Authorization and behavior |
|---|---|---|
| Obtain a CSRF token | `GET /csrf` | Available before sign-in; token is returned for the frontend to send on state-changing requests |
| Read current account and memberships | `GET /session` | Authenticated user; returns only that user's roles, families, and available teams |
| Start a session | `POST /session` | Validates credentials; creates a session and CSRF context |
| End a session | `DELETE /session` | Invalidates the current session |
| Accept an invitation | `POST /invitations/accept` | Validates one-time token and verified email; activates family membership atomically |
| Create an invitation | `POST /teams/{teamId}/invitations` | Team manager; creates an expiring invitation for an email and family |
| List available teams | `GET /teams` | Authenticated user; returns only teams with an active role or family membership |
| Create a team | `POST /clubs/{clubId}/teams` | Club administrator; validates club and season relationship |
| Read/edit a team | `GET, PATCH /teams/{teamId}` | Club administrator for administrative changes; team manager for team-scoped edits |
| Appoint team roles | `POST, DELETE /teams/{teamId}/roles` | Club administrator for manager assignments; team manager for the single parent-coordinator assignment |
| Manage players and families | `/teams/{teamId}/players`, `/teams/{teamId}/families` | Club administrator across the club; team manager only for the selected team |
| Read or edit match schedule | `/teams/{teamId}/matches` | Reads for authorized members; writes for team managers; removed matches retain task history |
| Preview/apply an import | `POST /teams/{teamId}/imports/{type}/preview`, `POST /teams/{teamId}/imports/{type}/{batchId}/apply` | Team manager; preview reports row validation and duplicates; apply revalidates and reports per-row outcome |
| Read dashboard and task lists | `GET /teams/{teamId}/dashboard`, `GET /families/{familyId}/assignments` | Dashboard requires active team or family membership; family tasks require active family membership |
| Claim washing | `POST /matches/{matchId}/washing/assignments` | Parent supplies the selected family; backend verifies active membership and atomically claims the single slot |
| Withdraw/correct washing | `DELETE /matches/{matchId}/washing/assignments/{assignmentId}` | Parent may withdraw only an assignment for their family; manager/coordinator may correct within their team |
| Complete washing | `POST /matches/{matchId}/washing/complete` | Assigned family, team manager, or parent coordinator |
| Manage catering posts | `/matches/{matchId}/catering/posts` | Team manager or parent coordinator, home matches only |
| Claim/withdraw catering slot | `/catering/posts/{postId}/assignments` | Parent supplies the selected family; backend verifies active membership and atomically checks capacity |
| Preview/confirm player transfer | `POST /clubs/{clubId}/player-transfers/preview`, `POST /clubs/{clubId}/player-transfers/confirm` | Club administrator; confirmation revalidates and applies all selected transfers or none |
| Archive a season | `POST /seasons/{seasonId}/archive` | Club administrator; preserves matches, assignments, and player history |

The operation list is intentionally domain-oriented. The implementation may
combine read operations where that improves the user workflow, but must preserve
the authorization and atomicity guarantees above.

## Request and Response Behavior

- Successful reads return JSON. Successful mutations return the resulting
  resource or a concise confirmation with its stable ID.
- A conflict caused by an already-filled slot, capacity limit, stale preview,
  active membership limit, or single-use invitation returns `409 Conflict`;
  the client refreshes affected state and explains that no conflicting change
  was applied.
- Invalid input returns `400` or `422` with stable error codes and field/row
  details. Import previews return per-row results; invalid rows do not prevent
  other valid rows from being applied.
- An unauthenticated request returns `401`. A user with no access to the
  requested team or family receives no protected data; inaccessible resource
  lookups may use `404` to avoid disclosing resource existence.
- Unexpected failures return a generic user-safe message and a correlation ID.
  Credentials, invitation tokens, and unnecessary personal data are excluded
  from logs and error responses.
- Repeating an idempotent remove/revoke operation does not recreate or transfer
  an assignment. A retry of a non-idempotent operation must not exceed any
  capacity or consume an invitation twice.

## UI Workflow Contract

- Parents get a mobile-first navigation for Start, Spiele, Waschen, Bewirtung,
  and Aufgaben, following the supplied `ui-mocks/`.
- The active team and role context remain visible when users switch teams or
  move between family and management workspaces.
- Free, partly filled, full, completed, and unavailable states are textually
  distinguishable, not communicated by color alone.
- A successful assignment displays confirmation. A capacity conflict refreshes
  availability and leaves the family unassigned.
- Administration provides separate entry points for teams, players/families,
  schedules, invitations, tasks, and seasons. A parent coordinator sees no
  roster or role-administration actions.
- File import always presents a preview, duplicate warnings, valid and invalid
  rows, and an explicit apply action. Existing task assignments are not
  silently changed.
