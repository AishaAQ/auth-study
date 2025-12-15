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
	
	private static final String DUMMY_PASSWORD_HASH =
		    Hashing.generateHash("dummy-password", Optional.empty());

	public UserService() {

	}
	
	public User register(String email, String password) {
		
		if (users.containsKey(email)) return null;

		String passwordHash = Hashing.generateHash(password, Optional.empty());
	        
		return users.put(email,new User(email,passwordHash));
		
	}
	
	public User authenticate(String email, String password) {
		
		User user = users.get(email);
		
	    String hashString = (user != null)
	            ? user.getPasswordHash()
	            : DUMMY_PASSWORD_HASH;
		
		boolean verified = Hashing.verifyPassword(password, hashString);
		
		return verified ? user : null;
	
	}
	
	public Collection<User> getUsers() {
		return users.values();
	}
	
}
