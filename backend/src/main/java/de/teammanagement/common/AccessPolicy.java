package de.teammanagement.common;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import de.teammanagement.auth.ClubRoleRepository;
import de.teammanagement.auth.ClubRoleType;
import de.teammanagement.auth.TeamUserDetails;
import de.teammanagement.families.FamilyAccessRepository;
import de.teammanagement.teams.TeamRepository;
import de.teammanagement.teams.TeamRoleAssignmentRepository;
import de.teammanagement.teams.TeamRoleType;

@Component
public class AccessPolicy {

	private final ClubRoleRepository clubRoles;
	private final FamilyAccessRepository familyAccess;
	private final TeamRepository teams;
	private final TeamRoleAssignmentRepository teamRoles;

	public AccessPolicy(ClubRoleRepository clubRoles,
			FamilyAccessRepository familyAccess,
			TeamRepository teams,
			TeamRoleAssignmentRepository teamRoles) {
		this.clubRoles = clubRoles;
		this.familyAccess = familyAccess;
		this.teams = teams;
		this.teamRoles = teamRoles;
	}

	public void requireClubAdministrator(UUID clubId, Authentication authentication) {
		var user = requireUser(authentication);
		if (!isClubAdministrator(clubId, user.id())) {
			throw resourceNotFound();
		}
	}

	public void requireTeamManager(UUID teamId, Authentication authentication) {
		var user = requireUser(authentication);
		var team = teams.findById(teamId).orElseThrow(AccessPolicy::resourceNotFound);
		var isClubAdministrator = isClubAdministrator(team.getClubId(), user.id());
		var isTeamManager = teamRoles.existsByTeamIdAndUserIdAndRoleAndActiveTrue(
				teamId, user.id(), TeamRoleType.TEAM_MANAGER);
		if (!isClubAdministrator && !isTeamManager) {
			throw resourceNotFound();
		}
	}

	public void requireTeamRole(UUID teamId, TeamRoleType role,
			Authentication authentication) {
		var user = requireUser(authentication);
		if (!teamRoles.existsByTeamIdAndUserIdAndRoleAndActiveTrue(teamId, user.id(), role)) {
			throw resourceNotFound();
		}
	}

	public void requireParentFamilyAccess(UUID familyId, UUID teamId,
			Authentication authentication) {
		var user = requireUser(authentication);
		if (!familyAccess.hasActiveParentAccess(user.id(), familyId, teamId)) {
			throw resourceNotFound();
		}
	}

	public TeamUserDetails requireUser(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()
				|| !(authentication.getPrincipal() instanceof TeamUserDetails user)) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Authentication required");
		}
		return user;
	}

	private boolean isClubAdministrator(UUID clubId, UUID userId) {
		return clubRoles.existsByClubIdAndUserIdAndRoleAndRevokedAtIsNull(
				clubId, userId, ClubRoleType.CLUB_ADMIN);
	}

	private static ApiException resourceNotFound() {
		return new ApiException(HttpStatus.NOT_FOUND, "Resource not found");
	}
}
