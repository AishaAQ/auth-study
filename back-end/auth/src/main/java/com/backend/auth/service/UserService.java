package com.backend.auth.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.auth.model.User;

import utilities.Hashing;

@Service
public class UserService {
	
	Map<String,User> users = new HashMap<>() {{
		put("aisha@gmail.com", new User("aisha@gmail.com","1234"));
		put("huda@gmail.com", new User("huda@gmail.com","1234"));
		
	}};


	public UserService() {

	}
	
	public User register(String email, String password) {

		String passwordHash = Hashing.generateHash(password, Optional.empty());
	        
		if (users.containsKey(email)) return null;
		
		return users.put(email,new User(email,passwordHash));
		
	}
	
	public User authenticate(String email, String password) {
		
		if (!users.containsKey(email)) return null;
		
		User user = users.get(email);
		
		boolean verified = Hashing.verifyPassword(password, user.getPasswordHash());
		
		return verified ? user : null;
	
	}
	
	public Collection<User> getUsers() {
		return users.values();
	}
	
}
