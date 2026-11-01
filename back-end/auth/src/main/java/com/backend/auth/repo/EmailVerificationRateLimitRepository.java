package com.backend.auth.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.backend.auth.model.EmailVerificationRateLimit;

import jakarta.persistence.LockModeType;

public interface EmailVerificationRateLimitRepository extends JpaRepository<EmailVerificationRateLimit, UUID> {
	
	// Spring ignores extra words between find and By so we can be descriptive there
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<EmailVerificationRateLimit> findWithLockByUserId(UUID userId);

}
