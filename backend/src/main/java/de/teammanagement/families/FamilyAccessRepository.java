package de.teammanagement.families;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FamilyAccessRepository {

	private static final String ACTIVE_PARENT_ACCESS_QUERY = """
			SELECT EXISTS (
				SELECT 1
				FROM family_member fm
				JOIN family f ON f.id = fm.family_id
				JOIN family_membership membership
					ON membership.family_id = f.id
					AND membership.team_id = ?
				JOIN parent_team_access access
					ON access.user_id = fm.user_id
					AND access.family_id = f.id
					AND access.team_id = membership.team_id
				JOIN user_account account ON account.id = fm.user_id
				WHERE fm.user_id = ?
					AND fm.family_id = ?
					AND fm.status = 'ACTIVE'
					AND f.status = 'ACTIVE'
					AND membership.status = 'ACTIVE'
					AND access.status = 'ACTIVE'
					AND account.status = 'ACTIVE'
			)
			""";

	private final JdbcTemplate jdbcTemplate;

	public FamilyAccessRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean hasActiveParentAccess(UUID userId, UUID familyId, UUID teamId) {
		var result = jdbcTemplate.queryForObject(ACTIVE_PARENT_ACCESS_QUERY,
				Boolean.class, teamId, userId, familyId);
		return Boolean.TRUE.equals(result);
	}
}
