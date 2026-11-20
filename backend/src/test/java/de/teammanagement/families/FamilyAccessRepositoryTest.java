package de.teammanagement.families;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

class FamilyAccessRepositoryTest {

	@Test
	void checksCurrentFamilyMembershipAndExplicitAccessForTheAuthenticatedUser() {
		var userId = UUID.randomUUID();
		var familyId = UUID.randomUUID();
		var teamId = UUID.randomUUID();
		var jdbcTemplate = mock(JdbcTemplate.class);
		when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class),
				eq(teamId), eq(userId), eq(familyId)))
				.thenReturn(true);
		var repository = new FamilyAccessRepository(jdbcTemplate);

		assertThat(repository.hasActiveParentAccess(userId, familyId, teamId)).isTrue();

		var query = ArgumentCaptor.forClass(String.class);
		verify(jdbcTemplate).queryForObject(query.capture(), eq(Boolean.class),
				eq(teamId), eq(userId), eq(familyId));
		assertThat(query.getValue())
				.contains("JOIN family_membership", "JOIN parent_team_access",
						"WHERE fm.user_id = ?", "AND fm.family_id = ?",
						"AND fm.status = 'ACTIVE'", "AND f.status = 'ACTIVE'",
						"AND membership.status = 'ACTIVE'",
						"AND access.status = 'ACTIVE'",
						"AND account.status = 'ACTIVE'");
	}

	@Test
	void returnsFalseWhenTheMembershipQueryHasNoActiveGrant() {
		var userId = UUID.randomUUID();
		var familyId = UUID.randomUUID();
		var teamId = UUID.randomUUID();
		var jdbcTemplate = mock(JdbcTemplate.class);
		when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class),
				eq(teamId), eq(userId), eq(familyId)))
				.thenReturn(false);
		var repository = new FamilyAccessRepository(jdbcTemplate);

		assertThat(repository.hasActiveParentAccess(userId, familyId, teamId)).isFalse();
	}
}
