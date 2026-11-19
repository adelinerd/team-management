package de.teammanagement.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamUserDetailsService implements UserDetailsService {

	private final UserAccountRepository userAccounts;

	public TeamUserDetailsService(UserAccountRepository userAccounts) {
		this.userAccounts = userAccounts;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) {
		return userAccounts.findByEmailIgnoreCase(username.trim())
				.map(TeamUserDetails::from)
				.orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
	}
}
