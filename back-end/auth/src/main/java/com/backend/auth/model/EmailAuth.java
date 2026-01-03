package com.backend.auth.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;

@Entity
public class EmailAuth {
	
    @Id
    private UUID userId;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId
    @JoinColumn(
    		name = "user_id",
    		unique = true,
            nullable = false,
            foreignKey = @ForeignKey(name = "email_auth_user_fk")
    )
	private User user;
	
	@Column(nullable = false)
	private String passwordHash;
	
	@Column(nullable = false)
	private int failedLoginAttempts = 0;
	
	@Nullable
	@Column(nullable = true)
	private LocalDateTime lockedUntil;
	
	public EmailAuth() {
		
	}
	
	public EmailAuth(User user, String passwordHash) {
		this.user = user;
		this.passwordHash = passwordHash;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public int getFailedLoginAttempts() {
		return failedLoginAttempts;
	}

	public void setFailedLoginAttempts(int failedLoginAttempts) {
		this.failedLoginAttempts = failedLoginAttempts;
	}

	public LocalDateTime getLockedUntil() {
		return lockedUntil;
	}

	public void setLockedUntil(LocalDateTime lockedUntil) {
		this.lockedUntil = lockedUntil;
	}

	public UUID getUserId() {
		return userId;
	}

	public User getUser() {
		return user;
	}

}
