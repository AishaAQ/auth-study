package com.backend.auth.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.backend.auth.utilities.SecureRandomId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Session {
	
	@Id
	@SecureRandomId
	@Column(updatable = false)
	private String sessionId;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
    		name = "user_id",
            nullable = false,
            foreignKey = @jakarta.persistence.ForeignKey(name = "session_user_fk")
    )
	private User user;
	
	@Column(nullable = false)
	@CreationTimestamp
	private Instant createdAt;
	
	@Column(nullable = false)
	private Instant expiresAt;
	
	private Instant lastSeenAt;
	
	private String userAgent;
	
	private String ipAddress;
	
	public Session() {
		
	}
	
	public Session(User user) {
		this.user = user;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Instant getLastSeenAt() {
		return lastSeenAt;
	}

	public void setLastSeenAt(Instant lastSeenAt) {
		this.lastSeenAt = lastSeenAt;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getSessionId() {
		return sessionId;
	}

	public User getUser() {
		return user;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

}
