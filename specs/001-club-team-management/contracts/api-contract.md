# API Contract: Vereinsweites Mannschaftsmanagement

**Date**: 2026-11-19
**Spec**: [spec.md](../spec.md)
**Data model**: [data-model.md](../data-model.md)

## OpenAPI workflow

The implementation will create and maintain `openapi.yaml` in this directory
as the authoritative machine-readable OpenAPI 3.0 contract. This Markdown
document records domain and security behavior; the OpenAPI file defines concrete
paths, operation IDs, parameters, request and response schemas, media types,
status codes, and session/CSRF requirements. Define each operation in OpenAPI
before implementing the corresponding endpoint or frontend integration.
Represent session-cookie authentication and the CSRF request header, including
their combined use on authenticated mutations; specify which operations are
available before authentication and how the CSRF endpoint returns the header
name and token. Describe import upload media types and row-level results, and
document validation, authentication, authorization, conflict, and unexpected
error responses. No credentials or real tokens belong in examples.

The backend will expose the same maintained specification at
`GET /api/v1/openapi.yaml` as API documentation to authenticated users.
Generate TypeScript API methods and models with a
pinned OpenAPI Generator `typescript-fetch` configuration; do not generate
React components or edit generated files. A separate handwritten adapter
provides same-origin credentials, CSRF acquisition and renewal, and error
handling. Generator output does not replace server-side authorization or
contract tests.

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
| Unauthenticated visitor | Sign-in, CSRF token acquisition, invitation acceptance, and public health status only |
| Club administrator | Club profile, seasons, teams, cross-team player/family/membership administration, and manager appointments; membership changes are audited |
| Team manager | Management operations for teams where the user has an active manager assignment |
| Parent coordinator | Washing and catering operations for the assigned team; no roster, family membership, or role administration |
| Parent | Read schedules and family assignments only with explicit parent access for that family and team; manage own family assignments only in non-archived seasons |

Every read and write operation checks the current user's role and the resource's
team/club relationship. Family membership and team-management roles are
independent. Users with multiple roles may use each authorized workflow, but a
management role alone does not grant family assignment rights.
Parent access requires active account, family membership, family-team membership,
and an explicit `ParentTeamAccess` grant for the requested family and team.
Family-task results are filtered per team, including historical results. Team
listing includes authorized administrative contexts for club administrators,
active management roles, and explicitly granted parent contexts. Coordinator
authority additionally requires current active parent eligibility in the team.
Archived matches and duties are read-only for every role and excluded from
current-task lists, even when incomplete.

## Operations

All paths below are relative to `/api/v1`. They describe the required resource
and command contract; define exact methods and payload fields in the
machine-readable contract, aligned with the data model.

| Operation | Method and path | Authorization and behavior |
|---|---|---|
| Obtain a CSRF token | `GET /csrf` | Available before sign-in; token is returned for the frontend to send on state-changing requests |
| Read current account and memberships | `GET /session` | Authenticated user; returns only that user's roles, families, and available teams |
| Start a session | `POST /session` | Validates credentials; creates a session and CSRF context |
| End a session | `DELETE /session` | Invalidates the current session |
| Accept an invitation | `POST /invitations/accept` | Validates token and verified email; requires already-active family and family-team membership; atomically links the parent and grants only the invited family/team access |
| Create an invitation | `POST /teams/{teamId}/invitations` | Team manager; creates an expiring invitation for an email and family |
| List available teams | `GET /teams` | Authenticated user; returns only explicitly authorized administrative, management, or parent contexts |
| Read/edit club profile | `GET, PATCH /clubs/{clubId}` | Club administrator after one-time setup; edits name, unique key, and time zone |
| Create a team | `POST /clubs/{clubId}/teams` | Club administrator; validates club and season relationship |
| Read/edit a team | `GET, PATCH /teams/{teamId}` | Club administrator for administrative changes; team manager for team-scoped edits |
| Appoint team roles | `POST, DELETE /teams/{teamId}/roles` | Club administrator for manager assignments; team manager for the single parent-coordinator assignment |
| Manage players and families | `/teams/{teamId}/players`, `/teams/{teamId}/families` | Club administrator across the club; team manager only for the selected team |
| List/create club players and families | `GET, POST /clubs/{clubId}/players`, `GET, POST /clubs/{clubId}/families` | Club administrator, including records without an active team assignment |
| Read/edit club player or family | `GET, PATCH /clubs/{clubId}/players/{playerId}`, `GET, PATCH /clubs/{clubId}/families/{familyId}` | Club administrator; validates club ownership |
| Reactivate family-team membership | `POST /teams/{teamId}/families/{familyId}/reactivate` | Explicit team-manager action, audited; invitation acceptance cannot perform it |
| Read or edit match schedule | `/teams/{teamId}/matches` | Reads for authorized members; writes for team managers; removed matches retain task history |
| Preview/apply an import | `POST /teams/{teamId}/imports/{type}/preview`, `POST /teams/{teamId}/imports/{type}/{batchId}/apply` | Team manager; preview reports row validation and duplicates; apply revalidates and reports per-row outcome |
| Read dashboard and task lists | `GET /teams/{teamId}/dashboard`, `GET /families/{familyId}/assignments` | Dashboard checks current team authorization; family tasks require explicit parent grants and filter results per authorized team |
| Claim washing | `POST /matches/{matchId}/washing/assignments` | Parent supplies the selected family; backend verifies active membership and atomically claims the single slot |
| Withdraw/correct washing | `DELETE /matches/{matchId}/washing/assignments/{assignmentId}` | Parent may withdraw only an assignment for their family; manager/coordinator may correct within their team |
| Complete washing | `POST /matches/{matchId}/washing/complete` | Assigned family, team manager, or parent coordinator |
| Manage catering posts | `/matches/{matchId}/catering/posts` | Team manager or parent coordinator, home matches only |
| Claim/withdraw catering slot | `/catering/posts/{postId}/assignments` | Parent supplies the selected family; backend verifies active membership and atomically checks capacity |
| Preview/confirm player transfer | `POST /clubs/{clubId}/player-transfers/preview`, `POST /clubs/{clubId}/player-transfers/confirm` | Club administrator; confirmation revalidates and applies all selected transfers or none |
| Archive a season | `POST /seasons/{seasonId}/archive` | Club administrator; preserves history, makes matches/duties read-only and removes them from current-task views |

The operation list is intentionally domain-oriented. The implementation may
combine read operations where that improves the user workflow, but must preserve
the authorization and atomicity guarantees above.
All family/player CRUD and import preview/apply operations require explicit
controller implementations and OpenAPI definitions. Administrative membership
changes, including import and transfer effects, write actor/time/record audit
events in the same transaction. An inactive family-team membership blocks
invitation acceptance without consuming the token; the manager must reactivate
it first. Archived match/duty mutations return a documented `409 Conflict`.

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
