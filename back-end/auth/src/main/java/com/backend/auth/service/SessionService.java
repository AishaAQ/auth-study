package com.backend.auth.service;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.backend.auth.config.AuthProperties;
import com.backend.auth.exceptions.AuthException;
import com.backend.auth.model.Session;
import com.backend.auth.model.User;
import com.backend.auth.repo.SessionRepository;

import jakarta.transaction.Transactional;

@Service
public class SessionService {
	
	private final SessionRepository sessionRepository;
	private final AuthProperties authProperties;
	
	private final UserService userService;
	
	public SessionService(SessionRepository sessionRepository, AuthProperties authProperties, UserService userService) {
		this.sessionRepository = sessionRepository;
		this.authProperties = authProperties;
		this.userService = userService;
	}
	
	@Transactional
	public String createSession(String email, String password, String userAgent, String ipAddress) {
		
		User authUser = userService.authenticate(email, password);		
		return createSession(authUser, userAgent, ipAddress);
		
	}

	@Transactional
	public String createSession(User user, String userAgent, String ipAddress) {
		
		Instant now = Instant.now();
		Session session = new Session(user);
		session.setExpiresAt(now.plus(authProperties.session().lifetime()));
		
		session.setUserAgent(userAgent);
		session.setIpAddress(ipAddress);
		session.setLastSeenAt(now);
		
		sessionRepository.save(session);
				
		return session.getSessionToken();
		
	}

	@Transactional
	public boolean isValidSession(String sessionToken) {
		
		Optional<Session> session = sessionRepository.findBySessionToken(sessionToken);
		
		if (session.isEmpty()) return false;
		
		Session currentSession = session.get();
		
		if (!currentSession.getExpiresAt().isAfter(Instant.now())) {
			sessionRepository.delete(currentSession);
			return false;
		}
		
		return true;
		
	}
	
	@Transactional
	public Optional<User> authenticate(String sessionToken) {
		
		Optional<Session> session = sessionRepository.findBySessionToken(sessionToken);
		
		if (session.isEmpty()) return Optional.empty();
		
		Session currentSession = session.get();
		
		if (!currentSession.getExpiresAt().isAfter(Instant.now())) {
			sessionRepository.delete(currentSession);
			return Optional.empty();
		}
		
		User user = currentSession.getUser();
		Hibernate.initialize(user);
		
		return Optional.of(user);
		
	}
	
	public Collection<Session> getSessions(UUID userId) {
		return sessionRepository.findByUserUserId(userId);
	}
	
	public boolean sessionExists(UUID sessionId, String sessionToken) {
		return sessionRepository.existsBySessionIdAndSessionToken(sessionId, sessionToken);
	}
	
	@Transactional
	public void deleteSession(String sessionToken) {
		sessionRepository.deleteBySessionToken(sessionToken);
	}
	
	@Transactional
	public void deleteSession(UUID sessionId, UUID userId) {
		Optional<Session> sessionOpt = sessionRepository.findById(sessionId);
		
		if (sessionOpt.isPresent()) {
			Session session = sessionOpt.get();
			if (session.getUser().getUserId().equals(userId)) {
				sessionRepository.delete(session);
			} else {
				throw new AuthException("Access Denied", HttpStatus.FORBIDDEN);
			}
		}
	}
	
	@Transactional
	public void deleteSessions(UUID userId) {
		sessionRepository.deleteByUserUserId(userId);
	}

}
