package de.teammanagement.teams;

import java.time.Instant;
import java.util.UUID;

import de.teammanagement.common.JpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "team")
public class Team extends JpaEntity {

	@NotNull
	@Column(name = "club_id", nullable = false)
	private UUID clubId;

	@NotNull
	@Column(name = "season_id", nullable = false)
	private UUID seasonId;

	@NotBlank
	@Column(nullable = false)
	private String name;

	private String description;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TeamStatus status = TeamStatus.ACTIVE;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected Team() {
	}

	public Team(UUID clubId, UUID seasonId, String name, String description) {
		this.clubId = clubId;
		this.seasonId = seasonId;
		this.name = name;
		this.description = description;
	}

	@PrePersist
	void initializeTimestamps() {
		createdAt = Instant.now();
		updatedAt = createdAt;
	}

	@PreUpdate
	void updateTimestamp() {
		updatedAt = Instant.now();
	}

	public UUID getClubId() {
		return clubId;
	}

	public UUID getSeasonId() {
		return seasonId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public TeamStatus getStatus() {
		return status;
	}

	public void update(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public void deactivate() {
		status = TeamStatus.INACTIVE;
	}
}
