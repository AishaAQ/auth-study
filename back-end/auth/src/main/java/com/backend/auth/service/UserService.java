package com.backend.auth.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.auth.exceptions.AuthException;
import com.backend.auth.exceptions.ResourceAlreadyExistsException;
import com.backend.auth.model.EmailAuth;
import com.backend.auth.model.User;
import com.backend.auth.repo.EmailAuthRepository;
import com.backend.auth.repo.UserRepository;
import com.backend.auth.utilities.Hashing;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	
    private final UserRepository userRepository;
    private final EmailAuthRepository emailAuthRepository;
	
	
	private static final String DUMMY_PASSWORD_HASH =
		    Hashing.generateHash("dummy-password", Optional.empty());

	public UserService(UserRepository userRepository, EmailAuthRepository emailAuthRepository) {
		this.userRepository = userRepository;
		this.emailAuthRepository = emailAuthRepository;
	}
	
	@Transactional
	public User register(String rawEmail, String password) {
		
		String email = rawEmail.toLowerCase();
		
		if (userRepository.existsByEmail(email)) throw new ResourceAlreadyExistsException("Email already registered");

		String passwordHash = Hashing.generateHash(password, Optional.empty());
		
        User user = new User(email);
        userRepository.save(user);
        
        EmailAuth emailAuth = new EmailAuth(user, passwordHash);
        emailAuthRepository.save(emailAuth);
	        
		return user;
		
	}
	
	public User authenticate(String rawEmail, String password) {
		
		Optional<EmailAuth> emailAuth = emailAuthRepository.findByUserEmail(rawEmail.toLowerCase());
		
		String hashString = emailAuth
                .map(EmailAuth::getPasswordHash)
                .orElse(DUMMY_PASSWORD_HASH);
		
		boolean verified = Hashing.verifyPassword(password, hashString);
		
	    return emailAuth
	            .filter(e -> verified)  
	            .map(EmailAuth::getUser)
	            .orElseThrow(() -> new AuthException("Invalid email or password"));
	
	}
	
	public Collection<User> getUsers() {
		return userRepository.findAll();
	}
	
}
