package com.backend.auth.service;

import java.time.Duration;
import java.time.Instant;

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
import com.backend.auth.utilities.SecureRandomCodeGenerator;

import jakarta.transaction.Transactional;

@Service
public class EmailVerificationService {
	
	public enum Result { VERIFIED, INVALID_CODE, CODE_EXPIRED }
	
	private final EmailVerificationTokenRepository tokenRepository;
	private final EmailVerificationRateLimitRepository rateLimitRepository;
	
	private final AuthProperties authProperties;
	
	public EmailVerificationService(
			EmailVerificationTokenRepository tokenRepository, 
			EmailVerificationRateLimitRepository rateLimitRepository,
			AuthProperties authProperties) {
		this.tokenRepository = tokenRepository;
		this.rateLimitRepository = rateLimitRepository;
		this.authProperties = authProperties;
	}
	
	@Transactional
	public void requestVerificationToken(User user) {
		
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
	public Result verifyEmail(User user, String submittedCode) {
		if (user.isEmailVerified()) return Result.VERIFIED;
		return Result.VERIFIED;
	}
	
	private EmailVerificationRateLimit getOrCreateRateLimit(User user) {
		return rateLimitRepository.findById(user.getUserId())
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
