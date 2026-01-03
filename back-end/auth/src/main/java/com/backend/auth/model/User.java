package com.backend.auth.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.UniqueElements;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_user")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(updatable = false)
	private UUID userId;
	
	
	private String email;
	
	private String passwordHash;
	
	private boolean isVerified;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	private LocalDateTime lastLogin;
	
	private LocalDateTime lastUpdated;
	
	private int failedLoginAttempts;
	
	@Nullable
	private LocalDateTime lockedUntil;
	
	public User(String email, String passwordHash) {
		this.email = email;
		this.passwordHash = passwordHash;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

}
