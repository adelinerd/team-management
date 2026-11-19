package de.teammanagement.club;

import java.time.Instant;
import java.time.ZoneId;

import de.teammanagement.common.JpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "club")
public class Club extends JpaEntity {

	@NotBlank
	@Column(nullable = false)
	private String name;

	@NotBlank
	@Column(name = "external_key", nullable = false, unique = true)
	private String externalKey;

	@NotBlank
	@Column(name = "time_zone", nullable = false)
	private String timeZone = "Europe/Berlin";

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected Club() {
	}

	public Club(String name, String externalKey, ZoneId timeZone) {
		this.name = name;
		this.externalKey = externalKey;
		this.timeZone = timeZone.getId();
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

	public String getName() {
		return name;
	}

	public String getExternalKey() {
		return externalKey;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void updateProfile(String name, String externalKey, ZoneId timeZone) {
		this.name = name;
		this.externalKey = externalKey;
		this.timeZone = timeZone.getId();
	}
}
