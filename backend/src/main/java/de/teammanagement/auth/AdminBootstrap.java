package de.teammanagement.auth;

import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.teammanagement.club.Club;
import de.teammanagement.club.ClubRepository;

@Component
public class AdminBootstrap implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(AdminBootstrap.class);

	private final ClubRepository clubs;
	private final UserAccountRepository users;
	private final ClubRoleRepository clubRoles;
	private final PasswordEncoder passwordEncoder;

	@Value("${bootstrap.initial-admin-enabled:false}")
	private boolean enabled;

	@Value("${bootstrap.club-name:}")
	private String clubName;

	@Value("${bootstrap.club-key:}")
	private String clubKey;

	@Value("${bootstrap.club-time-zone:Europe/Berlin}")
	private String clubTimeZone;

	@Value("${bootstrap.admin-email:}")
	private String adminEmail;

	@Value("${bootstrap.admin-password:}")
	private String adminPassword;

	public AdminBootstrap(ClubRepository clubs, UserAccountRepository users,
			ClubRoleRepository clubRoles,
			PasswordEncoder passwordEncoder) {
		this.clubs = clubs;
		this.users = users;
		this.clubRoles = clubRoles;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (!enabled) {
			return;
		}
		if (clubs.count() != 0) {
			logger.info("Initial administrator bootstrap skipped: a club already exists");
			return;
		}
		if (clubName.isBlank() || clubKey.isBlank() || adminEmail.isBlank()
				|| adminPassword.isBlank()) {
			throw new IllegalStateException(
					"Initial admin bootstrap requires club name/key and admin email/password");
		}

		var club = clubs.save(new Club(clubName, clubKey, ZoneId.of(clubTimeZone)));
		var user = users.save(new UserAccount(adminEmail,
				passwordEncoder.encode(adminPassword), null));
		clubRoles.save(new ClubRole(club.getId(), user.getId()));
		logger.info("Initial club and administrator created; disable initial admin bootstrap");
	}
}
