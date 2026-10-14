package com.backend.auth.repo;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.auth.model.Session;

public interface SessionRepository extends JpaRepository<Session, UUID> {
	
	void deleteBySessionToken(String sessionToken);
	void deleteByUserUserId(UUID userId);
	Optional<Session> findBySessionToken(String sessionToken);
	Collection<Session> findByUserUserId(UUID userId);
	boolean existsBySessionIdAndSessionToken(UUID sessionId, String sessionToken);
	

}
