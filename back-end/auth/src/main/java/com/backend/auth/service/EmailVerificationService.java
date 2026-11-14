package com.backend.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.backend.auth.config.AuthProperties;
import com.backend.auth.exceptions.AuthException;
import com.backend.auth.exceptions.RateLimitExceededException;
import com.backend.auth.model.EmailVerificationRateLimit;
import com.backend.auth.model.EmailVerificationToken;
import com.backend.auth.model.User;
import com.backend.auth.repo.EmailVerificationRateLimitRepository;
import com.backend.auth.repo.EmailVerificationTokenRepository;
import com.backend.auth.repo.UserRepository;
import com.backend.auth.utilities.SecureRandomCodeGenerator;

import jakarta.transaction.Transactional;

@Service
public class EmailVerificationService {
	
	public enum Result { VERIFIED, ALREADY_VERIFIED, WRONG_CODE, NO_ACTIVE_CODE }
	
	private final UserRepository userRepository;
	private final EmailVerificationTokenRepository tokenRepository;
	private final EmailVerificationRateLimitRepository rateLimitRepository;
	
	private final AuthProperties authProperties;
	
	public EmailVerificationService(
			UserRepository userRepository,
			EmailVerificationTokenRepository tokenRepository, 
			EmailVerificationRateLimitRepository rateLimitRepository,
			AuthProperties authProperties) {
		this.userRepository = userRepository;
		this.tokenRepository = tokenRepository;
		this.rateLimitRepository = rateLimitRepository;
		this.authProperties = authProperties;
	}
	
	@Transactional
	public void requestVerificationToken(UUID userId) {
		
		User user = userRepository.findById(userId)
	            .orElseThrow(() -> new IllegalStateException("User not found: " + userId));
		
		if (user.getEmail() == null) throw new AuthException("User has no email address", HttpStatus.CONFLICT);
		
		if (user.isEmailVerified()) throw new AuthException("Email already verified", HttpStatus.CONFLICT);
		
		Instant now = Instant.now();
		
		EmailVerificationRateLimit rateLimit = getOrCreateRateLimit(user);
		
		refreshAttemptWindow(rateLimit, now);
		validateAttemptsLeft(rateLimit, now);
		
		if (rateLimit.getLastRequestedAt() != null) {
			validateResendCooldown(rateLimit, now);
		}
		
		rateLimit.setLastRequestedAt(now);
		rateLimitRepository.save(rateLimit);
		
		tokenRepository.findById(user.getUserId()).ifPresent(tokenRepository::delete);
		tokenRepository.flush();
		EmailVerificationToken token = createToken(user, now);
		
	}
	
	@Transactional
	public Result verifyEmail(UUID userId, String submittedToken) {
		
		User user = userRepository.findById(userId)
	            .orElseThrow(() -> new IllegalStateException("User not found: " + userId));
		
		if (user.isEmailVerified()) return Result.ALREADY_VERIFIED;
		
		Instant now = Instant.now();
		
		EmailVerificationRateLimit rateLimit = rateLimitRepository
													.findWithLockByUserId(user.getUserId())
													.orElse(null);
		if (rateLimit == null) return Result.NO_ACTIVE_CODE;
		
		refreshAttemptWindow(rateLimit, now);
		validateAttemptsLeft(rateLimit, now);
		
		EmailVerificationToken token = tokenRepository
												.findById(user.getUserId())
												.orElse(null);
		if (token == null) return Result.NO_ACTIVE_CODE;
		
		if (token.isExpired() || !token.getEmail().equals(user.getEmail())) {
			tokenRepository.delete(token);
			return Result.NO_ACTIVE_CODE;
		}
		
		if (!codesMatch(token.getToken(),submittedToken)) {
			rateLimit.incrementFailedAttemptCounter();
			return Result.WRONG_CODE;
		}
		
		user.markEmailVerified();
		tokenRepository.delete(token);
		rateLimit.resetWindow(now);
		
		return Result.VERIFIED;
	}
	
	// For constant time equality check
	private boolean codesMatch(String stored, String submitted) {
	    return MessageDigest.isEqual(
	            stored.getBytes(StandardCharsets.UTF_8),
	            submitted.getBytes(StandardCharsets.UTF_8));
	}
	
	private EmailVerificationRateLimit getOrCreateRateLimit(User user) {
		return rateLimitRepository.findWithLockByUserId(user.getUserId())
				.orElseGet(() -> new EmailVerificationRateLimit(user));
	}
	
	private void validateResendCooldown(EmailVerificationRateLimit rateLimit, Instant now) {
		Duration resendCooldownDuration = authProperties.emailVerification().resendCooldown();
		Instant cooldownExpiry = rateLimit.getLastRequestedAt().plus(resendCooldownDuration);
		
		if (now.isBefore(cooldownExpiry)) {
			long millisLeft = Duration.between(now, cooldownExpiry).toMillis();
			long secondsLeft = Math.ceilDiv(millisLeft, 1000);
			throw new RateLimitExceededException(secondsLeft, String.format("Please wait %d seconds before requesting a new code.", secondsLeft));
		}
	}
	
	private EmailVerificationToken createToken(User user, Instant now) {
		String token = SecureRandomCodeGenerator.numeric(authProperties.emailVerification().tokenLength());
		
		Duration validityDuration = authProperties.emailVerification().tokenValidity();
		Instant expiresAt = now.plus(validityDuration);
		
		EmailVerificationToken emailVerificationToken = new EmailVerificationToken(token,user,user.getEmail(),expiresAt);
		tokenRepository.save(emailVerificationToken);
		return emailVerificationToken;
	}
	
	private void refreshAttemptWindow(EmailVerificationRateLimit rateLimit, Instant now) {
	    Duration window = authProperties.emailVerification().attemptWindow();
	    Instant windowEnd = rateLimit.getWindowStart().plus(window);

	    if (!now.isBefore(windowEnd)) {
	        rateLimit.resetWindow(now);
	    }
	}
	
	private void validateAttemptsLeft(EmailVerificationRateLimit rateLimit, Instant now) {
		if (rateLimit.getFailedAttemptCount() >= authProperties.emailVerification().maxAttempts()) {
	        Instant windowEnd = rateLimit.getWindowStart()
	                .plus(authProperties.emailVerification().attemptWindow());
			long millisLeft = Duration.between(now, windowEnd).toMillis();
			long secondsLeft = Math.ceilDiv(millisLeft, 1000);
	        throw new RateLimitExceededException(secondsLeft, String.format("Too many failed attempts. Try again in %d seconds.", secondsLeft));
		}
	}
}
