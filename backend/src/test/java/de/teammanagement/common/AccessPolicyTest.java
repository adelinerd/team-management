package de.teammanagement.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;

import de.teammanagement.auth.ClubRoleRepository;
import de.teammanagement.auth.ClubRoleType;
import de.teammanagement.auth.TeamUserDetails;
import de.teammanagement.auth.UserAccountStatus;
import de.teammanagement.families.FamilyAccessRepository;
import de.teammanagement.teams.Team;
import de.teammanagement.teams.TeamRepository;
import de.teammanagement.teams.TeamRoleAssignmentRepository;
import de.teammanagement.teams.TeamRoleType;

class AccessPolicyTest {

	@Test
	void teamManagerAuthorizationUsesTheTeamsPersistedClub() {
		var clubId = UUID.randomUUID();
		var teamId = UUID.randomUUID();
		var user = user();
		var clubRoles = mock(ClubRoleRepository.class);
		var familyAccess = mock(FamilyAccessRepository.class);
		var teams = mock(TeamRepository.class);
		var teamRoles = mock(TeamRoleAssignmentRepository.class);
		when(teams.findById(teamId))
				.thenReturn(Optional.of(new Team(clubId, UUID.randomUUID(), "Team", null)));
		when(clubRoles.existsByClubIdAndUserIdAndRoleAndRevokedAtIsNull(
				clubId, user.id(), ClubRoleType.CLUB_ADMIN))
				.thenReturn(false);
		when(teamRoles.existsByTeamIdAndUserIdAndRoleAndActiveTrue(
				teamId, user.id(), TeamRoleType.TEAM_MANAGER))
				.thenReturn(true);

		var policy = new AccessPolicy(clubRoles, familyAccess, teams, teamRoles);
		policy.requireTeamManager(teamId, authentication(user));

		verify(clubRoles).existsByClubIdAndUserIdAndRoleAndRevokedAtIsNull(
				clubId, user.id(), ClubRoleType.CLUB_ADMIN);
	}

	@Test
	void teamManagerAuthorizationDeniesUnknownOrUnassignedTeams() {
		var teamId = UUID.randomUUID();
		var user = user();
		var teams = mock(TeamRepository.class);
		when(teams.findById(teamId)).thenReturn(Optional.empty());

		var policy = new AccessPolicy(
				mock(ClubRoleRepository.class),
				mock(FamilyAccessRepository.class),
				teams,
				mock(TeamRoleAssignmentRepository.class));

		assertThatThrownBy(() -> policy.requireTeamManager(teamId, authentication(user)))
				.isInstanceOf(ApiException.class)
				.satisfies(exception -> assertThat(((ApiException) exception).getStatus())
						.isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND));
	}

	@Test
	void parentFamilyAuthorizationUsesTheAuthenticatedUserAndCurrentGrantLookup() {
		var familyId = UUID.randomUUID();
		var teamId = UUID.randomUUID();
		var user = user();
		var familyAccess = mock(FamilyAccessRepository.class);
		when(familyAccess.hasActiveParentAccess(user.id(), familyId, teamId)).thenReturn(true);
		var policy = new AccessPolicy(
				mock(ClubRoleRepository.class),
				familyAccess,
				mock(TeamRepository.class),
				mock(TeamRoleAssignmentRepository.class));

		policy.requireParentFamilyAccess(familyId, teamId, authentication(user));

		verify(familyAccess).hasActiveParentAccess(user.id(), familyId, teamId);
	}

	@Test
	void missingParentFamilyGrantIsNotFound() {
		var familyAccess = mock(FamilyAccessRepository.class);
		var policy = new AccessPolicy(
				mock(ClubRoleRepository.class),
				familyAccess,
				mock(TeamRepository.class),
				mock(TeamRoleAssignmentRepository.class));
		when(familyAccess.hasActiveParentAccess(
				org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
				org.mockito.ArgumentMatchers.any()))
				.thenReturn(false);

		assertThatThrownBy(() -> policy.requireParentFamilyAccess(
				UUID.randomUUID(), UUID.randomUUID(), authentication(user())))
				.isInstanceOf(ApiException.class);
	}

	private static TeamUserDetails user() {
		return new TeamUserDetails(UUID.randomUUID(), "parent@example.test", "hash",
				UserAccountStatus.ACTIVE);
	}

	private static TestingAuthenticationToken authentication(TeamUserDetails user) {
		return new TestingAuthenticationToken(user, null, List.of());
	}
}
