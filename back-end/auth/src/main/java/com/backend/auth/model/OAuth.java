package com.backend.auth.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

@Entity
public class OAuth {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(updatable = false)
	private UUID oauthId;
	
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
	private String provider;

	public OAuth() {
		
	}
}
