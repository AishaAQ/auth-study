package com.backend.auth.web.security;

import com.backend.auth.model.User;

public class SecurityContext {
	
	private static final ThreadLocal<User> currentUser = new ThreadLocal<>();
	
	public static void setCurrentUser(User user) {
		currentUser.set(user);
	}
	
	public static User getCurrentUser() {
		return currentUser.get();
	}
	
	public static void clear() {
		currentUser.remove();
	}

}
