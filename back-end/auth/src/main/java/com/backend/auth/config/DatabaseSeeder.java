package com.backend.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.backend.auth.model.User;
import com.backend.auth.model.UserRole;
import com.backend.auth.repo.UserRepository;
import com.backend.auth.service.UserService;

@Component
public class DatabaseSeeder implements CommandLineRunner {
	
	private final UserRepository userRepository;
	private final UserService userService;	

	public DatabaseSeeder(UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}
	
	@Override
	public void run(String... args) throws Exception {
		if (userRepository.count() == 0) {
			User admin = userService.register("admin@backend.com", "admin123");
			admin.setRole(UserRole.ADMIN);
			userRepository.save(admin);
		}
		
		
	}

}
