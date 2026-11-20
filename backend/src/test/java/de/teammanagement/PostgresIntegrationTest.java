package de.teammanagement;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PostgresIntegrationTest {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void appliesTheProductionLiquibaseChangelog() {
		var executedChangesets = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM databasechangelog WHERE id = ?",
				Long.class, "001-foundation-tables");

		assertThat(executedChangesets).isEqualTo(1L);
	}
}
