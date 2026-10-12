package com.backend.auth.web.dto;

import java.util.UUID;

public record UserProfileDTO(UUID userId, String email) {

}
