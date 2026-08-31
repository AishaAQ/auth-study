package com.backend.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.backend.auth.model.EmailVerificationToken;
import com.backend.auth.model.User;
import com.backend.auth.repo.EmailVerificationTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class EmailVerificationTokenService {
	
	private final EmailVerificationTokenRepository emailVerificationTokenRepository;
	
	public EmailVerificationTokenService(EmailVerificationTokenRepository emailVerificationTokenRepository) {
		this.emailVerificationTokenRepository = emailVerificationTokenRepository;
	}
	
	@Transactional 
	public String createToken(User user) {
		
		Instant expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES); 
		EmailVerificationToken token = new EmailVerificationToken(user, expiresAt); 
		emailVerificationTokenRepository.save(token); 
		return token.getToken(); 
		
	} 	

}
