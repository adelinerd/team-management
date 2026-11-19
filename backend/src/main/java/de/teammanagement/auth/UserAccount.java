package de.teammanagement.auth;

import java.time.Instant;

import de.teammanagement.common.JpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "user_account")
public class UserAccount extends JpaEntity {

	@Email
	@NotBlank
	@Column(nullable = false, unique = true)
	private String email;

	@NotBlank
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(name = "display_name")
	private String displayName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserAccountStatus status = UserAccountStatus.ACTIVE;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	protected UserAccount() {
	}

	public UserAccount(String email, String passwordHash, String displayName) {
		this.email = email.trim().toLowerCase(java.util.Locale.ROOT);
		this.passwordHash = passwordHash;
		this.displayName = displayName;
	}

	@PrePersist
	void initializeTimestamp() {
		createdAt = Instant.now();
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getDisplayName() {
		return displayName;
	}

	public UserAccountStatus getStatus() {
		return status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
