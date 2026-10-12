package com.backend.auth.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public record AuthProperties(
		Session session,
		List<String> publicPaths
) {
	public record Session(Duration lifetime) {}

}
