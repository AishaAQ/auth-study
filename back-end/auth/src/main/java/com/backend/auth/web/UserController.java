package com.backend.auth.web;

import java.util.Collection;

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
		userService.createUser(user.email, user.password);
	}
	
	@GetMapping("/users")
	public Collection<User> getUsers() {
		return userService.getUsers();
	}
	
	@PostMapping("/user")
	public User getUser(@RequestBody @Valid UserDTO user) {
		return userService.getUser(user.email, user.password);
	}

}
