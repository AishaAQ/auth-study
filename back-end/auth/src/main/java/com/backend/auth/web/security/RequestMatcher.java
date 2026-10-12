package com.backend.auth.web.security;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.backend.auth.config.AuthProperties;

@Component
public class RequestMatcher {
	
	private final AntPathMatcher pathMatcher = new AntPathMatcher();
	private final AuthProperties authProperties;
	
	public RequestMatcher(AuthProperties authProperties) {
		this.authProperties = authProperties;
	}
	
	public boolean isPublicRequest(String path, String method) {
		
		if ("OPTIONS".equalsIgnoreCase(method)) {
			return true;
		}
		
		return authProperties.publicPaths().stream().anyMatch(entry -> {
			if (entry.contains(":")) {
				String[] parts = entry.split(":",2);
				String expectedMethod = parts[0];
				String pathPattern = parts[1];
				
				return expectedMethod.equalsIgnoreCase(method) && pathMatcher.match(pathPattern, path);
			}
			return pathMatcher.match(entry, path);
		});
	}

}
