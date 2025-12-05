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
	
	
	
	public UserService() {

	}

	public void addUser(String email, String password) {
		users.put(email,new User(email,password));
	}
	
	public Collection<User> getUsers() {
		return users.values();
	}
	
}
