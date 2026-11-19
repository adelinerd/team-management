package de.teammanagement.common;

import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.teammanagement.auth.TeamUserDetails;

@Service
public class AuditService {

	private final AuditEventRepository events;

	public AuditService(AuditEventRepository events) {
		this.events = events;
	}

	@Transactional
	public AuditEvent record(UUID clubId, Authentication authentication, String eventType,
			String entityType, UUID entityId, String safeDetails) {
		if (authentication == null || !(authentication.getPrincipal() instanceof TeamUserDetails user)) {
			throw new AccessDeniedException("Authenticated actor required for audit");
		}
		return events.save(new AuditEvent(clubId, user.id(), eventType,
				entityType, entityId, safeDetails));
	}
}
