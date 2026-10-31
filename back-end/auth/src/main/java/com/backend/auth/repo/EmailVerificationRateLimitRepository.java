package com.backend.auth.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.auth.model.EmailVerificationRateLimit;

public interface EmailVerificationRateLimitRepository extends JpaRepository<EmailVerificationRateLimit, UUID> {

}
