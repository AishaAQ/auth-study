package com.backend.auth.service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
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
	public User register(String email, String password) {
		
		String normalizedEmail = email.toLowerCase();
		
		if (userRepository.existsByEmail(normalizedEmail)) throw new ResourceAlreadyExistsException("Email already registered");

		String passwordHash = Hashing.generateHash(password, Optional.empty());
		
        User user = new User(normalizedEmail);
        userRepository.save(user);
        
        EmailAuth emailAuth = new EmailAuth(user, passwordHash);
        emailAuthRepository.save(emailAuth);
	        
		return user;
		
	}
	
	public User authenticate(String email, String password) {
		
		String normalizedEmail = email.toLowerCase();
		
		Optional<EmailAuth> emailAuth = emailAuthRepository.findByUserEmail(normalizedEmail);
		
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
	
	@Transactional
	public User changePassword(UUID userId, String currentPassword, String newPassword) {
				
		Optional<EmailAuth> emailAuthOpt = emailAuthRepository.findById(userId);
		
		if (emailAuthOpt.isEmpty()) throw new AuthException("No password set for this account", HttpStatus.BAD_REQUEST);
		
		EmailAuth emailAuth = emailAuthOpt.get();
		
		String hashString = emailAuth.getPasswordHash();
		
		boolean verified = Hashing.verifyPassword(currentPassword, hashString);
		if (!verified) throw new AuthException("Current password is incorrect", HttpStatus.FORBIDDEN);
		
		if (currentPassword.equals(newPassword)) throw new AuthException("New password must not be same as current password", HttpStatus.CONFLICT);
		
		String passwordHash = Hashing.generateHash(newPassword, Optional.empty());
		emailAuth.setPasswordHash(passwordHash);
        	        
	    return emailAuth.getUser();
	}
	
	public Optional<User> findUser(UUID userId) {
		return userRepository.findById(userId);
	}
	
}
