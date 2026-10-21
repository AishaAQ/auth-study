package com.backend.auth.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.backend.auth.model.User;

import jakarta.transaction.Transactional;

@Service
public class AccountService {
	
    private final UserService userService;
    private final SessionService sessionService;
	
	public AccountService(UserService userService, SessionService sessionService) {
		this.userService = userService;
		this.sessionService = sessionService;
	}
	
	@Transactional
	public String changePassword(UUID userId, String currentPassword, String newPassword,
							   String userAgent, String ipAddress) {
		
		User user = userService.changePassword(userId, currentPassword, newPassword);
		sessionService.deleteSessions(userId);
		return sessionService.createSession(user, userAgent, ipAddress);
		
	}
	
}
