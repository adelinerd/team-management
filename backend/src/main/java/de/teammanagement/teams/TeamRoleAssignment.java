package de.teammanagement.teams;

import java.time.Instant;
import java.util.UUID;

import de.teammanagement.common.JpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "team_role_assignment")
public class TeamRoleAssignment extends JpaEntity {

	@NotNull
	@Column(name = "team_id", nullable = false)
	private UUID teamId;

	@NotNull
	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TeamRoleType role;

	@Min(1)
	@Max(3)
	@Column(name = "manager_slot")
	private Integer managerSlot;

	@Column(nullable = false)
	private boolean active = true;

	@Column(name = "appointed_by")
	private UUID appointedBy;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	@Column(name = "revoked_at")
	private Instant revokedAt;

	protected TeamRoleAssignment() {
	}

	public TeamRoleAssignment(UUID teamId, UUID userId, TeamRoleType role,
			Integer managerSlot, UUID appointedBy) {
		this.teamId = teamId;
		this.userId = userId;
		this.role = role;
		this.managerSlot = managerSlot;
		this.appointedBy = appointedBy;
	}

	public UUID getTeamId() {
		return teamId;
	}

	public UUID getUserId() {
		return userId;
	}

	public TeamRoleType getRole() {
		return role;
	}

	public Integer getManagerSlot() {
		return managerSlot;
	}

	public boolean isActive() {
		return active;
	}

	public UUID getAppointedBy() {
		return appointedBy;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getRevokedAt() {
		return revokedAt;
	}

	public void revoke() {
		active = false;
		revokedAt = Instant.now();
	}
}
