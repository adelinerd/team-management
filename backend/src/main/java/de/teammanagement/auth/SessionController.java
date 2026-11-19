package de.teammanagement.auth;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.teammanagement.common.ApiException;
import de.teammanagement.teams.TeamRoleAssignmentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Validated
public class SessionController {

	private final AuthenticationManager authenticationManager;
	private final SecurityContextRepository securityContexts;
	private final HttpSessionCsrfTokenRepository csrfTokens;
	private final UserAccountRepository users;
	private final ClubRoleRepository clubRoles;
	private final TeamRoleAssignmentRepository teamRoles;

	public SessionController(AuthenticationManager authenticationManager,
			SecurityContextRepository securityContexts,
			HttpSessionCsrfTokenRepository csrfTokens,
			UserAccountRepository users,
			ClubRoleRepository clubRoles,
			TeamRoleAssignmentRepository teamRoles) {
		this.authenticationManager = authenticationManager;
		this.securityContexts = securityContexts;
		this.csrfTokens = csrfTokens;
		this.users = users;
		this.clubRoles = clubRoles;
		this.teamRoles = teamRoles;
	}

	@GetMapping("/csrf")
	public CsrfResponse csrf(HttpServletRequest request) {
		var token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (token == null) {
			throw new IllegalStateException("CSRF token was not initialized by Spring Security");
		}
		return new CsrfResponse(token.getHeaderName(), token.getToken());
	}

	@GetMapping("/session")
	public SessionResponse currentSession(Authentication authentication) {
		var principal = authenticatedPrincipal(authentication);
		return responseFor(authentication, principal);
	}

	@PostMapping("/session")
	public SessionResponse signIn(@Valid @RequestBody SignInRequest requestBody,
			HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					UsernamePasswordAuthenticationToken.unauthenticated(
							requestBody.email(), requestBody.password()));
		} catch (AuthenticationException exception) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		}

		var existingSession = request.getSession(false);
		if (existingSession == null) {
			throw new IllegalStateException("A CSRF-protected sign-in requires an existing session");
		}
		request.changeSessionId();
		csrfTokens.saveToken(null, request, response);
		var context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
		securityContexts.saveContext(context, request, response);
		return responseFor(authentication);
	}

	@DeleteMapping("/session")
	public ResponseEntity<Void> signOut(HttpServletRequest request,
			HttpServletResponse response) {
		csrfTokens.saveToken(null, request, response);
		var session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		SecurityContextHolder.clearContext();
		return ResponseEntity.noContent().build();
	}

	private SessionResponse responseFor(Authentication authentication) {
		return responseFor(authentication, authenticatedPrincipal(authentication));
	}

	private SessionResponse responseFor(Authentication authentication, TeamUserDetails principal) {
		var account = users.findById(principal.id())
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
		var roles = new ArrayList<String>();
		clubRoles.findAllByUserIdAndRevokedAtIsNull(principal.id())
				.forEach(role -> roles.add(role.getRole().name()));
		teamRoles.findAllByUserIdAndActiveTrue(principal.id())
				.forEach(role -> roles.add(role.getRole().name()));
		return new SessionResponse(principal.id(), principal.email(),
				account.getDisplayName(), roles.stream().distinct().sorted().toList());
	}

	private TeamUserDetails authenticatedPrincipal(Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof TeamUserDetails user) {
			return user;
		}
		throw new ApiException(HttpStatus.UNAUTHORIZED, "Authentication required");
	}
}
