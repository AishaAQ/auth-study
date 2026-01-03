package com.backend.auth.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.auth.model.EmailAuth;

@Repository
public interface EmailAuthRepository extends JpaRepository<EmailAuth, UUID> {

    Optional<EmailAuth> findByUserEmail(String email); 
    
}
