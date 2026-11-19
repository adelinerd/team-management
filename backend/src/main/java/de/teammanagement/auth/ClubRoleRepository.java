package de.teammanagement.auth;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRoleRepository extends JpaRepository<ClubRole, UUID> {

	boolean existsByClubIdAndUserIdAndRoleAndRevokedAtIsNull(
			UUID clubId, UUID userId, ClubRoleType role);

	List<ClubRole> findAllByUserIdAndRevokedAtIsNull(UUID userId);
}
