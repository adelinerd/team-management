package de.teammanagement;

import org.springframework.boot.SpringApplication;

public class TestTeamManagementApplication {

	public static void main(String[] args) {
		SpringApplication.from(TeamManagementApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
