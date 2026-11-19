package de.teammanagement.auth;

import java.io.IOException;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		var provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	AuthenticationManager authenticationManager(DaoAuthenticationProvider provider) {
		return authentication -> provider.authenticate(authentication);
	}

	@Bean
	HttpSessionCsrfTokenRepository csrfTokenRepository() {
		var repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-CSRF-TOKEN");
		return repository;
	}

	@Bean
	SecurityContextRepository securityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	}

	@Bean
	AuthenticationEntryPoint apiAuthenticationEntryPoint(ObjectMapper objectMapper) {
		return (request, response, exception) -> writeProblem(objectMapper, request, response,
				HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
				"Authentication required");
	}

	@Bean
	AccessDeniedHandler apiAccessDeniedHandler(ObjectMapper objectMapper) {
		return (request, response, exception) -> writeProblem(objectMapper, request, response,
				HttpServletResponse.SC_NOT_FOUND, "Not Found", "Resource not found");
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http,
			HttpSessionCsrfTokenRepository csrfTokenRepository,
			SecurityContextRepository securityContextRepository,
			AuthenticationEntryPoint apiAuthenticationEntryPoint,
			AccessDeniedHandler apiAccessDeniedHandler) throws Exception {
		http
				.csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository))
				.securityContext(context -> context.securityContextRepository(securityContextRepository))
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
						.sessionFixation(fixation -> fixation.changeSessionId()))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/api/v1/csrf", "/error").permitAll()
						.requestMatchers(org.springframework.http.HttpMethod.POST,
								"/api/v1/session", "/api/v1/invitations/accept").permitAll()
						.anyRequest().authenticated())
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(apiAuthenticationEntryPoint)
						.accessDeniedHandler(apiAccessDeniedHandler))
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults());
		return http.build();
	}

	private static void writeProblem(ObjectMapper objectMapper, HttpServletRequest request,
			HttpServletResponse response, int status, String title, String detail)
			throws IOException {
		var id = UUID.randomUUID().toString();
		response.setStatus(status);
		response.setHeader("X-Correlation-ID", id);
		response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
		response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		var problem = ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(status), detail);
		problem.setTitle(title);
		problem.setInstance(java.net.URI.create(request.getRequestURI()));
		problem.setProperty("correlationId", id);
		objectMapper.writeValue(response.getOutputStream(), problem);
	}
}
