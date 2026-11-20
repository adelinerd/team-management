package de.teammanagement.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import de.teammanagement.auth.TeamUserDetails;
import de.teammanagement.auth.UserAccountStatus;

class AuditServiceTest {

	@Test
	void storesOnlySortedNonSensitiveFieldNames() {
		var events = mock(AuditEventRepository.class);
		when(events.save(any(AuditEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));
		var service = new AuditService(events);
		var actor = user();

		var event = service.record(UUID.randomUUID(), authentication(actor),
				"TEAM_UPDATED", "Team", UUID.randomUUID(),
				List.of("status", "managerSlot"));

		assertThat(ReflectionTestUtils.getField(event, "details"))
				.isEqualTo("[\"managerSlot\",\"status\"]");
		verify(events).save(event);
	}

	@Test
	void rejectsSensitiveAuditFieldsAndMetadata() {
		var events = mock(AuditEventRepository.class);
		var service = new AuditService(events);
		var authentication = authentication(user());

		assertThatThrownBy(() -> service.record(UUID.randomUUID(), authentication,
				"TEAM_UPDATED", "Team", UUID.randomUUID(), List.of("email")))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> service.record(UUID.randomUUID(), authentication,
				"password=secret", "Team", UUID.randomUUID(), List.of("status")))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void requiresAnAuthenticatedAuditActor() {
		var service = new AuditService(mock(AuditEventRepository.class));

		assertThatThrownBy(() -> service.record(UUID.randomUUID(), null,
				"TEAM_UPDATED", "Team", UUID.randomUUID(), List.of("status")))
				.isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
	}

	private static TeamUserDetails user() {
		return new TeamUserDetails(UUID.randomUUID(), "admin@example.test", "hash",
				UserAccountStatus.ACTIVE);
	}

	private static TestingAuthenticationToken authentication(TeamUserDetails user) {
		return new TestingAuthenticationToken(user, null, List.of());
	}
}
