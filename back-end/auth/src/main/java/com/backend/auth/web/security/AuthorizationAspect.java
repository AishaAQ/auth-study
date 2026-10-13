package com.backend.auth.web.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.backend.auth.exceptions.AuthException;
import com.backend.auth.model.User;
import com.backend.auth.model.UserRole;

@Aspect
@Component
public class AuthorizationAspect {
	
	@Before("@annotation(requiresRole)")
	public void authorize(RequiresRole requiresRole) {
		
		User currentUser = SecurityContext.getCurrentUser();
		
		if (currentUser == null) {
			throw new AuthException("Access Denied.");
		}
		
		UserRole requiredRole = requiresRole.value();
		if (currentUser.getRole() != requiredRole) {
			throw new AuthException("Access Denied.");
		}
	}

}
