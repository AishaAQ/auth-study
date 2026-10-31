package com.backend.auth.utilities;

import java.security.SecureRandom;

public final class SecureRandomCodeGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private SecureRandomCodeGenerator() {
    	
    }
    
    public static String numeric(int digits) {
    	
    	if (digits < 1 || digits > 9) {
    		throw new IllegalArgumentException("digits must be between 1 and 9");
    	}
    	int bound = (int) Math.pow(10, digits);
    	
    	int code = SECURE_RANDOM.nextInt(bound);
    	
    	return String.format("%0"+ digits + "d", code);
    }

}