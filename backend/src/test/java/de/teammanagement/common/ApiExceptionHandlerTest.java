package de.teammanagement.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

class ApiExceptionHandlerTest {

	private final ApiExceptionHandler handler = new ApiExceptionHandler();

	@Test
	void unexpectedErrorsReturnGenericDetailsAndTheRequestCorrelationId() {
		var request = new MockHttpServletRequest("GET", "/api/v1/resource");
		var correlationId = UUID.randomUUID();
		request.setAttribute(CorrelationIdFilter.ATTRIBUTE, correlationId);

		var response = handler.handleUnexpected(
				new IllegalStateException("private implementation detail"), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody().getDetail()).isEqualTo("An unexpected error occurred");
		assertThat(response.getBody().getProperties())
				.containsEntry("correlationId", correlationId.toString());
	}

	@Test
	void invalidCorrelationAttributesAreReplacedWithUuid() {
		var request = new MockHttpServletRequest("GET", "/api/v1/resource");
		request.setAttribute(CorrelationIdFilter.ATTRIBUTE, "untrusted-correlation-value");

		var response = handler.handleUnexpected(new IllegalStateException("failure"), request);
		var correlationId = response.getBody().getProperties().get("correlationId");
		var parsedCorrelationId = UUID.fromString(correlationId.toString());

		assertThat(request.getAttribute(CorrelationIdFilter.ATTRIBUTE))
				.isEqualTo(parsedCorrelationId);
	}
}
