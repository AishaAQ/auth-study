package com.backend.auth.utilities;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.EnumSet;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

@SuppressWarnings("serial")
public class SecureRandomIdGenerator implements BeforeExecutionGenerator {
	
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	@Override
	public EnumSet<EventType> getEventTypes() {
		
		return EnumSet.of(EventType.INSERT);
	}

	@Override
	public String generate(SharedSessionContractImplementor session, Object owner, Object currentValue,
			EventType eventType) {
		
		SecureRandom secureRandom = new SecureRandom();
		byte[] bytes = new byte[32];
		SECURE_RANDOM.nextBytes(bytes);
		
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

}
