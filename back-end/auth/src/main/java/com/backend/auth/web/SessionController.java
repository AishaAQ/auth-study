package com.backend.auth.web;

import java.util.Collection;
import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.auth.config.AuthProperties;
import com.backend.auth.model.Session;
import com.backend.auth.model.User;
import com.backend.auth.model.UserRole;
import com.backend.auth.service.SessionService;
import com.backend.auth.web.dto.UserDTO;
import com.backend.auth.web.dto.UserProfileDTO;
import com.backend.auth.web.security.RequiresRole;
import com.backend.auth.web.security.SecurityContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class SessionController {
	
	private final SessionService sessionService;
	private final AuthProperties authProperties;
	
	public SessionController(SessionService sessionService, AuthProperties authProperties) {
		this.sessionService = sessionService;
		this.authProperties = authProperties;
	}
	
	@PostMapping("/api/auth/sessions")
	public ResponseEntity<?> create(@RequestBody @Valid UserDTO user, HttpServletRequest request, HttpServletResponse response) {
		
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null || userAgent.isBlank()) {
			userAgent = "Unknown Device/Script";
		}
		
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.isBlank()) {
			ipAddress = request.getRemoteAddr();
		} else {
			ipAddress = ipAddress.split(",")[0].trim();
		}
		
		String sessionToken = sessionService.createSession(user.email, user.password, userAgent, ipAddress);
		
		ResponseCookie cookie = ResponseCookie.from("SessionToken", sessionToken)
		        .httpOnly(true)
		        .secure(true)
		        .path("/")
		        .maxAge(authProperties.session().lifetime())
		        .sameSite("Lax")
		        .build();

		response.addHeader("Set-Cookie", cookie.toString());

	    return ResponseEntity.ok().build();
		
	}
	
	@GetMapping("/api/auth/sessions")
	public Collection<Session> getSessions() {
		UUID currentUserId = SecurityContext.getCurrentUser().getUserId();
		return sessionService.getSessions(currentUserId);
	}
	
	@DeleteMapping("/api/auth/sessions/current")
	public ResponseEntity<?> delete(@CookieValue(value = "SessionToken", required = false) String sessionToken, 
			HttpServletResponse response) {
		
	    if (sessionToken != null) {
	        sessionService.deleteSession(sessionToken);
	    }
	    
	    clearSessionCookie(response);

	    return ResponseEntity.noContent().build();
		
	}
	
	@DeleteMapping("/api/auth/sessions")
	public ResponseEntity<Void> deleteAllUserSessions(HttpServletResponse response) {
		User currentUser = SecurityContext.getCurrentUser();
		sessionService.deleteSessions(currentUser.getUserId());
		clearSessionCookie(response);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/api/auth/sessions/{sessionId}")
	public ResponseEntity<Void> deleteSession(
			@PathVariable UUID sessionId,
			@CookieValue(value = "SessionToken", required = false) String currentSessionToken,
			HttpServletResponse response) {
		User currentUser = SecurityContext.getCurrentUser();
		boolean wasCurrentSession = sessionService.sessionExists(sessionId, currentSessionToken);
		sessionService.deleteSession(sessionId, currentUser.getUserId());
		if (wasCurrentSession) clearSessionCookie(response);
		return ResponseEntity.noContent().build();
	}
	
	private void clearSessionCookie(HttpServletResponse response) {
	    ResponseCookie cookie = ResponseCookie.from("SessionToken", "")
	            .httpOnly(true)
	            .secure(true)
	            .path("/")
	            .maxAge(0)
	            .sameSite("Lax")
	            .build();
	    response.addHeader("Set-Cookie", cookie.toString());
	}

}
