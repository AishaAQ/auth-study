package com.backend.auth.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.auth.config.AuthProperties;
import com.backend.auth.model.User;
import com.backend.auth.service.EmailVerificationService;
import com.backend.auth.service.EmailVerificationService.Result;
import com.backend.auth.service.SessionService;
import com.backend.auth.web.dto.VerifyEmailDTO;
import com.backend.auth.web.security.SecurityContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class EmailVerificationController {
	
	private final EmailVerificationService emailVerificationService;
	private final SessionService sessionService;
	
	private final AuthProperties authProperties;
	
	public EmailVerificationController(
							EmailVerificationService emailVerificationService, 
							SessionService sessionService,
							AuthProperties authProperties) {
		this.emailVerificationService = emailVerificationService;
		this.sessionService = sessionService;
		this.authProperties = authProperties;
	}
	
	@PostMapping("/api/me/email-verification")
	public ResponseEntity<Void> requestEmailVerification() {
		User currentUser = SecurityContext.getCurrentUser();
		emailVerificationService.requestVerificationToken(currentUser.getUserId());
		return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/api/me/email-verification")
	public ResponseEntity<?> verifyEmail(
								@RequestBody @Valid VerifyEmailDTO verifyEmailDTO, 			
								HttpServletRequest request,
								HttpServletResponse response) {
		
		UUID userId = SecurityContext.getCurrentUser().getUserId();
		
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
		
		Result verificationResult = emailVerificationService.verifyEmail(userId, verifyEmailDTO.token());
		
		return switch (verificationResult) {
			case VERIFIED -> {
				
				String sessionToken = sessionService.resetSessions(userId, userAgent, ipAddress);
				
				ResponseCookie cookie = ResponseCookie.from("SessionToken", sessionToken)
				        .httpOnly(true)
				        .secure(true)
				        .path("/")
				        .maxAge(authProperties.session().lifetime())
				        .sameSite("Lax")
				        .build();

				response.addHeader("Set-Cookie", cookie.toString());

			   yield ResponseEntity
			    		.status(HttpStatus.NO_CONTENT)
			    		.build();
				
			}
			
			case ALREADY_VERIFIED -> ResponseEntity.noContent().build();
			
			case WRONG_CODE -> ResponseEntity.badRequest()
										.body(Map.of("error", "Incorrect token", "code", "WRONG_CODE"));
			
			case NO_ACTIVE_CODE -> ResponseEntity.badRequest()
            							.body(Map.of("error", "No active token. Request a new one.", "code", "NO_ACTIVE_CODE"));
		};
		
	}

}
