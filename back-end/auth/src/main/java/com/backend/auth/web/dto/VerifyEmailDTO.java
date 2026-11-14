package com.backend.auth.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailDTO(
        @NotBlank
        @Pattern(regexp = "\\d{8}", message = "Token must be 8 digits")
        String token) {}