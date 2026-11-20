package de.teammanagement.common;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Year;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.teammanagement.auth.ClubRole;
import de.teammanagement.auth.ClubRoleRepository;
import de.teammanagement.auth.UserAccount;
import de.teammanagement.auth.UserAccountRepository;
import de.teammanagement.club.Club;
import de.teammanagement.club.ClubRepository;
import de.teammanagement.club.Season;
import de.teammanagement.club.SeasonRepository;
import de.teammanagement.teams.Team;
import de.teammanagement.teams.TeamRepository;
import de.teammanagement.teams.TeamRoleAssignment;
import de.teammanagement.teams.TeamRoleAssignmentRepository;
import de.teammanagement.teams.TeamRoleType;

@Component
@Profile("local")
public class LocalSeed implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(LocalSeed.class);

	private final ClubRepository clubs;
	private final SeasonRepository seasons;
	private final TeamRepository teams;
	private final UserAccountRepository users;
	private final ClubRoleRepository clubRoles;
	private final TeamRoleAssignmentRepository teamRoles;
	private final PasswordEncoder passwordEncoder;

	@Value("${local.seed.enabled:false}")
	private boolean enabled;

	@Value("${local.seed.club-name:Local Football Club}")
	private String clubName;

	@Value("${local.seed.club-key:local}")
	private String clubKey;

	@Value("${local.seed.admin-email:}")
	private String adminEmail;

	@Value("${local.seed.admin-password:}")
	private String adminPassword;

	public LocalSeed(ClubRepository clubs, SeasonRepository seasons, TeamRepository teams,
			UserAccountRepository users, ClubRoleRepository clubRoles,
			TeamRoleAssignmentRepository teamRoles, PasswordEncoder passwordEncoder) {
		this.clubs = clubs;
		this.seasons = seasons;
		this.teams = teams;
		this.users = users;
		this.clubRoles = clubRoles;
		this.teamRoles = teamRoles;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (!enabled) {
			return;
		}
		if (clubs.count() != 0) {
			logger.info("Local seed skipped: a club already exists");
			return;
		}
		if (adminEmail.isBlank() || adminPassword.isBlank()) {
			throw new IllegalStateException(
					"Local seed requires LOCAL_ADMIN_EMAIL and LOCAL_ADMIN_PASSWORD");
		}

		var club = clubs.save(new Club(clubName, clubKey, ZoneId.of("Europe/Berlin")));
		var admin = users.save(new UserAccount(adminEmail,
				passwordEncoder.encode(adminPassword), "Local Administrator"));
		clubRoles.save(new ClubRole(club.getId(), admin.getId()));

		var year = Year.now();
		var season = seasons.save(new Season(club.getId(), year + "/" + year.plusYears(1),
				LocalDate.of(year.getValue(), 7, 1),
				LocalDate.of(year.plusYears(1).getValue(), 6, 30)));
		var team = teams.save(new Team(club.getId(), season.getId(), "U11", null));
		teamRoles.save(new TeamRoleAssignment(team.getId(), admin.getId(),
				TeamRoleType.TEAM_MANAGER, 1, admin.getId()));
		logger.info("Local club, season, team, and administrator fixtures created");
	}
}
