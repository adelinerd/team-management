package de.teammanagement.common;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "audit_event")
public class AuditEvent extends JpaEntity {

	@NotNull
	@Column(name = "club_id", nullable = false)
	private UUID clubId;

	@NotNull
	@Column(name = "actor_user_id", nullable = false)
	private UUID actorUserId;

	@NotBlank
	@Column(name = "event_type", nullable = false)
	private String eventType;

	@NotBlank
	@Column(name = "entity_type", nullable = false)
	private String entityType;

	@NotNull
	@Column(name = "entity_id", nullable = false)
	private UUID entityId;

	@Column(name = "occurred_at", nullable = false, updatable = false)
	private Instant occurredAt;

	@NotBlank
	@Column(nullable = false, columnDefinition = "text")
	private String details;

	protected AuditEvent() {
	}

	public AuditEvent(UUID clubId, UUID actorUserId, String eventType,
			String entityType, UUID entityId, String details) {
		this.clubId = clubId;
		this.actorUserId = actorUserId;
		this.eventType = eventType;
		this.entityType = entityType;
		this.entityId = entityId;
		this.details = details;
	}

	@PrePersist
	void setOccurredAt() {
		occurredAt = Instant.now();
	}
}
