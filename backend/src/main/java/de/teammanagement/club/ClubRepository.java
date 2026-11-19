package de.teammanagement.club;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, UUID> {

	boolean existsByExternalKey(String externalKey);
}
