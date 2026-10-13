package com.backend.auth.exceptions;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class AuthException extends RuntimeException {
	
	private final HttpStatus httpStatus;
	
    public AuthException(String message) {
        super(message);
        this.httpStatus = HttpStatus.UNAUTHORIZED;
    }
    
    public AuthException(String message, HttpStatus status) {
    	super(message);
    	this.httpStatus = status;
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.UNAUTHORIZED;
    }
    
    public HttpStatus getHttpStatus() {
    	return httpStatus;
    }

}
