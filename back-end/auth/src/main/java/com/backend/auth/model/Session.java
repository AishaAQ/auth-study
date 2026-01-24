package com.backend.auth.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.backend.auth.utilities.SessionIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Session {
	
	@Id
	@SessionIdGenerator
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
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private LocalDateTime expiresAt;
	
	private LocalDateTime lastSeenAt;
	
	private String userAgent;
	
	private String ipAddress;
	
	public Session() {
		
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public LocalDateTime getLastSeenAt() {
		return lastSeenAt;
	}

	public void setLastSeenAt(LocalDateTime lastSeenAt) {
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

}
