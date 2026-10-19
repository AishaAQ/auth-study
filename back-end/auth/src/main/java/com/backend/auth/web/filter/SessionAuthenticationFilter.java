package com.backend.auth.web.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.backend.auth.model.User;
import com.backend.auth.service.SessionService;
import com.backend.auth.web.security.RequestMatcher;
import com.backend.auth.web.security.SecurityContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter{
	
	private final SessionService sessionService;
	private final RequestMatcher requestMatcher;
	private final RequestMappingHandlerMapping handlerMapping;
	
	public SessionAuthenticationFilter(SessionService sessionService, RequestMatcher requestMatcher, RequestMappingHandlerMapping handlerMapping) {
		this.sessionService = sessionService;
		this.requestMatcher = requestMatcher;
		this.handlerMapping = handlerMapping;
	}
	

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String path = request.getRequestURI();
		String method = request.getMethod();
		
		// Skip authentication for public routes
		if (requestMatcher.isPublicRequest(path, method)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		// Extract session token
		String sessionToken = null;		
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("SessionToken".equals(cookie.getName())) {
					sessionToken = cookie.getValue();
					break;
				}
			}
		}
	
		// return 401 if session token absent
		if (sessionToken == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return;
		}
		
		// Verify session token
		Optional<User> authenticatedUser = sessionService.authenticate(sessionToken);	
		if (authenticatedUser.isEmpty()) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return;
		}
		
		try {
			SecurityContext.setCurrentUser(authenticatedUser.get());
			filterChain.doFilter(request, response);
		} finally {
			SecurityContext.clear();
		}
		

		
	}

}
