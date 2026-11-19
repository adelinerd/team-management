package de.teammanagement.common;

import java.util.LinkedHashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ProblemDetail> handleApiException(ApiException exception,
			HttpServletRequest request) {
		return problem(exception.getStatus(), exception.getMessage(), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ProblemDetail> handleValidation(
			MethodArgumentNotValidException exception, HttpServletRequest request) {
		var errors = new LinkedHashMap<String, String>();
		exception.getBindingResult().getFieldErrors().stream()
				.map(error -> (FieldError) error)
				.forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
		var response = create(HttpStatus.BAD_REQUEST, "Request validation failed", request);
		response.setProperty("errors", errors);
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException exception,
			HttpServletRequest request) {
		return problem(HttpStatus.NOT_FOUND, "Resource not found", request);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ProblemDetail> handleMalformedRequest(
			HttpMessageNotReadableException exception, HttpServletRequest request) {
		return problem(HttpStatus.BAD_REQUEST, "Request body is invalid", request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ProblemDetail> handleUnexpected(Exception exception,
			HttpServletRequest request) {
		logger.error("Unhandled API exception (correlationId={})",
				request.getAttribute(CorrelationIdFilter.ATTRIBUTE), exception);
		return problem(HttpStatus.INTERNAL_SERVER_ERROR,
				"An unexpected error occurred", request);
	}

	private ResponseEntity<ProblemDetail> problem(HttpStatus status, String detail,
			HttpServletRequest request) {
		return ResponseEntity.status(status).body(create(status, detail, request));
	}

	private ProblemDetail create(HttpStatus status, String detail,
			HttpServletRequest request) {
		var response = ProblemDetail.forStatusAndDetail(status, detail);
		response.setTitle(status.getReasonPhrase());
		response.setInstance(java.net.URI.create(request.getRequestURI()));
		var correlationId = request.getAttribute(CorrelationIdFilter.ATTRIBUTE);
		response.setProperty("correlationId",
				correlationId == null ? UUID.randomUUID().toString() : correlationId.toString());
		return response;
	}
}
