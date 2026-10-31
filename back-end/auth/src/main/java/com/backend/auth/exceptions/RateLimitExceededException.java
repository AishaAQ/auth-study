package com.backend.auth.exceptions;

public class RateLimitExceededException extends RuntimeException {
	
	private final long secondsRemaining;
	
	public RateLimitExceededException(long secondsRemaining, String message) {
		super(message);
		this.secondsRemaining = secondsRemaining;
	}
	
	public long getSecondsRemaining() {
		return secondsRemaining;
	}

}
