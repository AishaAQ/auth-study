package com.backend.auth.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

@Entity
public class EmailVerificationToken {

    @Id
    private UUID userId;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId
    @JoinColumn(
    		name = "user_id",
    		unique = true,
            nullable = false,
            foreignKey = @ForeignKey(name = "email_verification_token_user_fk")
    )
	private User user;

	@Column(updatable = false)
    private String token;
	
	@Column(nullable = false, updatable = false)
	private String email;

    @Column(nullable = false, updatable = false)
    private Instant expiresAt;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

	public EmailVerificationToken() {
		
	}
    
    public EmailVerificationToken(String token, User user, String email, Instant expiresAt) {
		this.token = token;
		this.user = user;
		this.email = email;
		this.expiresAt = expiresAt;
	}

	public boolean isExpired() {
    	return !expiresAt.isAfter(Instant.now());    	
    }

	public UUID getUserId() {
		return userId;
	}

	public String getToken() {
		return token;
	}

	public User getUser() {
		return user;
	}

	public String getEmail() {
		return email;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
  
}
