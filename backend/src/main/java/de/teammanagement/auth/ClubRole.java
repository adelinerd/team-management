package de.teammanagement.auth;

import java.time.Instant;
import java.util.UUID;

import de.teammanagement.common.JpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "club_role")
public class ClubRole extends JpaEntity {

	@NotNull
	@Column(name = "club_id", nullable = false)
	private UUID clubId;

	@NotNull
	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ClubRoleType role = ClubRoleType.CLUB_ADMIN;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	@Column(name = "revoked_at")
	private Instant revokedAt;

	protected ClubRole() {
	}

	public ClubRole(UUID clubId, UUID userId) {
		this.clubId = clubId;
		this.userId = userId;
	}

	public UUID getClubId() {
		return clubId;
	}

	public UUID getUserId() {
		return userId;
	}

	public ClubRoleType getRole() {
		return role;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getRevokedAt() {
		return revokedAt;
	}

	public void revoke() {
		if (revokedAt == null) {
			revokedAt = Instant.now();
		}
	}
}
