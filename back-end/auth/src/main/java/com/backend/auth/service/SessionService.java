package com.backend.auth.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.auth.config.SessionProperties;
import com.backend.auth.model.Session;
import com.backend.auth.model.User;
import com.backend.auth.repo.SessionRepository;

import jakarta.transaction.Transactional;

@Service
public class SessionService {
	
	private final SessionRepository sessionRepository;
	private final SessionProperties sessionProperties;
	
	private final UserService userService;
	
	public SessionService(SessionRepository sessionRepository, SessionProperties sessionProperties, UserService userService) {
		this.sessionRepository = sessionRepository;
		this.sessionProperties = sessionProperties;
		this.userService = userService;
	}
	
	@Transactional
	public String createSession(String email, String password) {
		
		User authUser = userService.authenticate(email, password);		
		return createSession(authUser);
		
	}

	@Transactional
	public String createSession(User user) {
		
		Instant now = Instant.now();
		Session session = new Session(user);
		session.setExpiresAt(now.plus(sessionProperties.lifetime()));
		sessionRepository.save(session);
				
		return session.getSessionId();
		
	}

	@Transactional
	public boolean isValidSession(String sessionId) {
		
		Optional<Session> session = sessionRepository.findById(sessionId);
		
		if (session.isEmpty()) return false;
		
		Session currentSession = session.get();
		
		if (!currentSession.getExpiresAt().isAfter(Instant.now())) {
			sessionRepository.delete(currentSession);
			return false;
		}
		
		return true;
		
	}
	
	@Transactional
	public void deleteSession(String sessionId) {
		
		sessionRepository.deleteById(sessionId);	
		
	}

}
