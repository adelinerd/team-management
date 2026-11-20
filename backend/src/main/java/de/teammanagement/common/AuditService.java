package de.teammanagement.common;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.teammanagement.auth.TeamUserDetails;

@Service
public class AuditService {

	private static final Pattern SENSITIVE_FIELD = Pattern.compile(
			".*(password|token|secret|email|phone|address|contact|name|birth|personal).*",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern FIELD_NAME = Pattern.compile("[A-Za-z][A-Za-z0-9.]*");
	private static final Pattern SAFE_METADATA = Pattern.compile("[A-Za-z][A-Za-z0-9_.-]*");

	private final AuditEventRepository events;

	public AuditService(AuditEventRepository events) {
		this.events = events;
	}

	@Transactional
	public AuditEvent record(UUID clubId, Authentication authentication, String eventType,
			String entityType, UUID entityId, List<String> changedFieldNames) {
		if (authentication == null || !authentication.isAuthenticated()
				|| !(authentication.getPrincipal() instanceof TeamUserDetails user)) {
			throw new AccessDeniedException("Authenticated actor required for audit");
		}
		validateSafeMetadata(eventType);
		validateSafeMetadata(entityType);
		var details = serializeChangedFields(changedFieldNames);
		return events.save(new AuditEvent(clubId, user.id(), eventType,
				entityType, entityId, details));
	}

	private void validateSafeMetadata(String value) {
		if (value == null || !SAFE_METADATA.matcher(value).matches()
				|| SENSITIVE_FIELD.matcher(value).matches()) {
			throw new IllegalArgumentException(
					"Audit metadata may contain only non-sensitive identifiers");
		}
	}

	private String serializeChangedFields(List<String> changedFieldNames) {
		Objects.requireNonNull(changedFieldNames, "changedFieldNames");
		var safeFieldNames = changedFieldNames.stream()
				.map(fieldName -> Objects.requireNonNull(fieldName, "fieldName").trim())
				.peek(fieldName -> {
					if (!FIELD_NAME.matcher(fieldName).matches()
							|| SENSITIVE_FIELD.matcher(fieldName).matches()) {
						throw new IllegalArgumentException(
								"Audit details may contain only non-sensitive field names");
					}
				})
				.distinct()
				.sorted()
				.toList();
		return safeFieldNames.stream()
				.map(fieldName -> "\"" + fieldName + "\"")
				.collect(java.util.stream.Collectors.joining(",", "[", "]"));
	}
}
