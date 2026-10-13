package com.backend.auth.web;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.auth.config.AuthProperties;
import com.backend.auth.model.User;
import com.backend.auth.service.SessionService;
import com.backend.auth.web.dto.UserDTO;
import com.backend.auth.web.dto.UserProfileDTO;
import com.backend.auth.web.security.SecurityContext;

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
	public ResponseEntity<?> create(@RequestBody @Valid UserDTO user, HttpServletResponse response) {
		
		String sessionToken = sessionService.createSession(user.email, user.password);
		
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
	
	@DeleteMapping("/api/auth/sessions/current")
	public ResponseEntity<?> delete(@CookieValue(value = "SessionToken", required = false) String sessionToken, 
			HttpServletResponse response) {
		
	    if (sessionToken != null) {
	        sessionService.deleteSession(sessionToken);
	    }
	    
	    ResponseCookie cookie = ResponseCookie.from("SessionToken", "")
	            .httpOnly(true)
	            .secure(true)
	            .path("/")
	            .maxAge(0)
	            .sameSite("Lax")
	            .build();

		response.addHeader("Set-Cookie", cookie.toString());

	    return ResponseEntity.noContent().build();
		
	}
	

}
