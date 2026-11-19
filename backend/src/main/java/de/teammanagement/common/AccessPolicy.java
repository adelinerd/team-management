package de.teammanagement.common;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import de.teammanagement.auth.ClubRoleRepository;
import de.teammanagement.auth.ClubRoleType;
import de.teammanagement.auth.TeamUserDetails;
import de.teammanagement.teams.TeamRoleAssignmentRepository;
import de.teammanagement.teams.TeamRoleType;

@Component
public class AccessPolicy {

	private final ClubRoleRepository clubRoles;
	private final TeamRoleAssignmentRepository teamRoles;

	public AccessPolicy(ClubRoleRepository clubRoles,
			TeamRoleAssignmentRepository teamRoles) {
		this.clubRoles = clubRoles;
		this.teamRoles = teamRoles;
	}

	public void requireClubAdministrator(UUID clubId, Authentication authentication) {
		var user = requireUser(authentication);
		if (!clubRoles.existsByClubIdAndUserIdAndRoleAndRevokedAtIsNull(
				clubId, user.id(), ClubRoleType.CLUB_ADMIN)) {
			throw new ApiException(HttpStatus.NOT_FOUND, "Resource not found");
		}
	}

	public void requireTeamRole(UUID teamId, TeamRoleType role,
			Authentication authentication) {
		var user = requireUser(authentication);
		if (!teamRoles.existsByTeamIdAndUserIdAndRoleAndActiveTrue(teamId, user.id(), role)) {
			throw new ApiException(HttpStatus.NOT_FOUND, "Resource not found");
		}
	}

	public TeamUserDetails requireUser(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()
				|| !(authentication.getPrincipal() instanceof TeamUserDetails user)) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Authentication required");
		}
		return user;
	}
}
