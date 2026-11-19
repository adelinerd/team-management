package de.teammanagement.auth;

import java.util.List;
import java.util.UUID;

public record SessionResponse(UUID userId, String email, String displayName,
		List<String> roles) {
}
