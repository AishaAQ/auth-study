package com.backend.auth.model;

import java.time.Instant;

import com.backend.auth.utilities.SecureRandomNumericId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class EmailVerificationToken {

    @Id
    @SecureRandomNumericId
	@Column(updatable = false)
    private String token;

    @OneToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

	public EmailVerificationToken() {
		
	}
	
    public EmailVerificationToken(User user, Instant expiresAt) {
        this.user = user;
        this.expiresAt = expiresAt;
    }

	public String getToken() {
		return token;
	}

	public User getUser() {
		return user;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}
	  
}
