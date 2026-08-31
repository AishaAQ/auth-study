package com.backend.auth.utilities;

import java.security.SecureRandom;
import java.util.EnumSet;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

public class SecureRandomNumericIdGenerator implements BeforeExecutionGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }

    @Override
    public String generate(
            SharedSessionContractImplementor session,
            Object owner,
            Object currentValue,
            EventType eventType) {

        int code = SECURE_RANDOM.nextInt(100_000_000);

        return String.format("%08d", code);
    }
}