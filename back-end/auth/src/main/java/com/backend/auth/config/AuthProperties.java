package com.backend.auth.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public record AuthProperties(
		Session session,
		List<String> publicPaths,
		EmailVerification emailVerification
) {
	public record Session(Duration lifetime) {}
	public record EmailVerification(
			Duration tokenValidity,
			Duration resendCooldown,
			int maxAttempts,
			Duration attemptWindow,
			int tokenLength
			) {}

}
