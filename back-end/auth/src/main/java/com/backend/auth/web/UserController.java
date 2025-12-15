package com.backend.auth.web;

import java.util.Collection;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.auth.model.User;
import com.backend.auth.service.UserService;
import com.backend.auth.web.dto.UserDTO;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {
	
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/users")
	public void register(@RequestBody @Valid UserDTO user) {
		userService.register(user.email, user.password);
	}
	
	@GetMapping("/users")
	public Collection<User> getUsers() {
		return userService.getUsers();
	}
	
	//TODO NEED TO UPDATE WHEN SESSION TOKENS IMPLEMENTED
	@PostMapping("/session-tokens")
	public ResponseEntity<?> authenticate(@RequestBody @Valid UserDTO user) {
		
		User authUser = userService.authenticate(user.email, user.password);
		
		if (authUser == null) {
	        return ResponseEntity
	        		.status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of("error", "Invalid email or password"));
		}
		
		return ResponseEntity.ok(authUser);
		
	}

}
