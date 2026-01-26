package com.backend.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.auth.model.Session;

public interface SessionRepository extends JpaRepository<Session, String> {
	
	boolean existsBySessionId(String sessionId);

}
