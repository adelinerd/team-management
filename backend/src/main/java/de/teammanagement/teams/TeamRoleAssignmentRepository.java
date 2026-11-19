package de.teammanagement.teams;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRoleAssignmentRepository extends JpaRepository<TeamRoleAssignment, UUID> {

	boolean existsByTeamIdAndUserIdAndRoleAndActiveTrue(
			UUID teamId, UUID userId, TeamRoleType role);

	List<TeamRoleAssignment> findAllByUserIdAndActiveTrue(UUID userId);
}
