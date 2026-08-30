package com.backend.auth.service;

import org.springframework.stereotype.Service;

import com.backend.auth.model.Session;
import com.backend.auth.model.User;
import com.backend.auth.repo.SessionRepository;

import jakarta.transaction.Transactional;

@Service
public class SessionService {
	
	private final SessionRepository sessionRepository;
	
	private final UserService userService;
	
	public SessionService(SessionRepository sessionRepository, UserService userService) {
		this.sessionRepository = sessionRepository;
		this.userService = userService;
	}
	
	@Transactional
	public String createSession(String email, String password) {
		
		User authUser = userService.authenticate(email, password);
		
		Session session = new Session(authUser);
		sessionRepository.save(session);
		
		return session.getSessionId();
		
	}

	@Transactional
	public String createSession(User user) {
		
		Session session = new Session(user);
		sessionRepository.save(session);
		
		return session.getSessionId();
		
	}

	public boolean isValidSession(String sessionId) {
		
		return sessionRepository.existsBySessionId(sessionId);
		
	}
	
	@Transactional
	public void deleteSession(String sessionId) {
		
		sessionRepository.deleteById(sessionId);	
		
	}

}
