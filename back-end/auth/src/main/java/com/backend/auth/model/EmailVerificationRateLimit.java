package com.backend.auth.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

@Entity
public class EmailVerificationRateLimit {
	
    @Id
    private UUID userId;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId
    @JoinColumn(
    		name = "user_id",
    		unique = true,
            nullable = false,
            foreignKey = @ForeignKey(name = "email_verification_rate_limit_user_fk")
    )
	private User user;
	
	@Column
	private Instant lastRequestedAt;
	
	@Column(nullable = false)
	private int failedAttemptCount = 0;
	
	@Column(nullable = false)
	private Instant windowStart;
	
	public EmailVerificationRateLimit() {
		
	}

	public EmailVerificationRateLimit(User user) {
		this.user = user;
		Instant now = Instant.now();
		this.windowStart = now;
	}
	
	public void incrementFailedAttemptCounter() {
		this.failedAttemptCount++;
	}
	
	public void resetWindow(Instant now) {
		failedAttemptCount = 0;
		windowStart = now;
	}

	public UUID getUserId() {
		return userId;
	}

	public User getUser() {
		return user;
	}

	public Instant getLastRequestedAt() {
		return lastRequestedAt;
	}

	public int getFailedAttemptCount() {
		return failedAttemptCount;
	}

	public Instant getWindowStart() {
		return windowStart;
	}

	public void setLastRequestedAt(Instant lastRequestedAt) {
		this.lastRequestedAt = lastRequestedAt;
	}

	public void setWindowStart(Instant windowStart) {
		this.windowStart = windowStart;
	}

}
