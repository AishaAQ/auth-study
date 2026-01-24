package com.backend.auth.web;

import java.util.Collection;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.auth.model.User;
import com.backend.auth.service.SessionService;
import com.backend.auth.service.UserService;
import com.backend.auth.web.dto.UserDTO;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {
	
	private final UserService userService;
	private final SessionService sessionService;
	
	public UserController(UserService userService, SessionService sessionService) {
		this.userService = userService;
		this.sessionService = sessionService;
	}

	@PostMapping("/users")
	public ResponseEntity<?> register(@RequestBody @Valid UserDTO user, HttpServletResponse response) {
		User newUser = userService.register(user.email, user.password);
		String sessionToken = sessionService.createSession(newUser);
		
		ResponseCookie cookie = ResponseCookie.from("SessionToken", sessionToken)
		        .httpOnly(true)
		        .secure(true)
		        .path("/")
		        .maxAge(3600)
		        .sameSite("Lax")
		        .build();

		response.addHeader("Set-Cookie", cookie.toString());

//	    URI location = URI.create("/users/" + newUser.getId()); // optional Location header
	    Map<String, Object> responseBody = Map.of(
	        "email", newUser.getEmail()
	    );

	    return ResponseEntity
	    		.status(HttpStatus.CREATED)
	    		.body(responseBody);
	}
	
	@GetMapping("/users")
	public Collection<User> getUsers() {
		return userService.getUsers();
	}
	
}
