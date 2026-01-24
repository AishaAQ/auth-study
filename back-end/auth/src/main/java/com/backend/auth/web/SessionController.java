package com.backend.auth.web;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.auth.service.SessionService;
import com.backend.auth.web.dto.UserDTO;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class SessionController {
	
	private final SessionService sessionService;
	
	public SessionController(SessionService sessionService) {
		this.sessionService = sessionService;
	}
	
	@PostMapping("/sessions")
	public ResponseEntity<?> create(@RequestBody @Valid UserDTO user, HttpServletResponse response) {
		
		String sessionToken = sessionService.createSession(user.email, user.password);
		
		ResponseCookie cookie = ResponseCookie.from("SessionToken", sessionToken)
		        .httpOnly(true)
		        .secure(true)
		        .path("/")
		        .maxAge(3600)
		        .sameSite("Lax")
		        .build();

		response.addHeader("Set-Cookie", cookie.toString());

	    return ResponseEntity.ok().build();
		
	}

}
