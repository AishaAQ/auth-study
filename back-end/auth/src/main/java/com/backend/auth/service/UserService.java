package com.backend.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
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
	
	private byte[] generateSalt16Byte() {
	    SecureRandom secureRandom = new SecureRandom();
	    byte[] salt = new byte[16];
	    secureRandom.nextBytes(salt);
	        
	    return salt;
	}

	public User createUser(String email, String password) {
		
		byte[] salt = generateSalt16Byte();
		
	    int iterations = 2;
	    int memLimit = 66536;
	    int hashLength = 32;
	    int parallelism = 1;
	        
	    Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
	      .withVersion(Argon2Parameters.ARGON2_VERSION_13)
	      .withIterations(iterations)
	      .withMemoryAsKB(memLimit)
	      .withParallelism(parallelism)
	      .withSalt(salt);
	        
	    Argon2BytesGenerator generate = new Argon2BytesGenerator();
	    generate.init(builder.build());
	    byte[] result = new byte[hashLength];
	    generate.generateBytes(password.getBytes(StandardCharsets.UTF_8), result, 0, result.length);
		
		if (users.containsKey(email)) return null;
		
		return users.put(email,new User(email,result.toString()));
	}
	
	public Collection<User> getUsers() {
		return users.values();
	}
	
}
