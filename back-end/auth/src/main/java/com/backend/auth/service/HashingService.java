package com.backend.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.stereotype.Service;

@Service
public class HashingService {
	
//	Argon2BytesGenerator generator = new Argon2BytesGenerator();
//	Argon2Parameters.Builder builder = new Argon2Parameters.Builder();
//	byte[] salt = new byte[16];
//	
//	public HashingService() {
//		
//	}
//	
//	private byte[] generateSalt() {
//		
//	}
//	
//	public String hash(String password) {
//		
//	}
//	
//	public String hash(String password, String salt) {
//		
//	}
//	
	
	
	
	
//	Argon2Parameters.Builder builder;
//	
//    int iterations = 2;
//    int memLimit = 66536;
//    int hashLength = 32;
//    int parallelism = 1;
//	
//	public HashingService() {
//		
//		byte[] salt = generateSalt16Byte();
//		
//	    builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
//	  	      .withVersion(Argon2Parameters.ARGON2_VERSION_13)
//	  	      .withIterations(iterations)
//	  	      .withMemoryAsKB(memLimit)
//	  	      .withParallelism(parallelism)
//	  	      .withSalt(salt);
//	}
//	
//	
//	private byte[] generateSalt16Byte() {
//	    SecureRandom secureRandom = new SecureRandom();
//	    byte[] salt = new byte[16];
//	    secureRandom.nextBytes(salt);
//	        
//	    return salt;
//	}
//	
//	public String hashPassword(String password) {
//	    Argon2BytesGenerator generate = new Argon2BytesGenerator();
//	    generate.init(builder.build());
//	    byte[] result = new byte[hashLength];
//	    generate.generateBytes(password.getBytes(StandardCharsets.UTF_8), result, 0, result.length);
//	    return result.toString();
//	}

}
