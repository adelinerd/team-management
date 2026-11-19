package de.teammanagement.club;

import java.time.LocalDate;
import java.util.UUID;

import de.teammanagement.common.JpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "season")
public class Season extends JpaEntity {

	@NotNull
	@Column(name = "club_id", nullable = false)
	private UUID clubId;

	@NotBlank
	@Column(nullable = false)
	private String name;

	@NotNull
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@NotNull
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SeasonStatus status;

	protected Season() {
	}

	public Season(UUID clubId, String name, LocalDate startDate, LocalDate endDate) {
		this.clubId = clubId;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = SeasonStatus.PLANNED;
	}

	public UUID getClubId() {
		return clubId;
	}

	public String getName() {
		return name;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public SeasonStatus getStatus() {
		return status;
	}
}
