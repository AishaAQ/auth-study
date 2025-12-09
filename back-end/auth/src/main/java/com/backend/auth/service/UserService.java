package com.backend.auth.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.backend.auth.model.User;

@Service
public class UserService {
	
	Map<String,User> users = new HashMap<>() {{
		put("aisha@gmail.com", new User("aisha@gmail.com","1234"));
		put("huda@gmail.com", new User("huda@gmail.com","1234"));
		
	}};
	
	PasswordHashingService passwordHashingService;

	public UserService(PasswordHashingService passwordHashingService) {
		this.passwordHashingService = passwordHashingService;

	}
	
	public User createUser(String email, String password) {
		

		String passwordHash = passwordHashingService.hashPassword(password);
	        
		if (users.containsKey(email)) return null;
		
		return users.put(email,new User(email,passwordHash));
	}
	
	public Collection<User> getUsers() {
		return users.values();
	}
	
}
