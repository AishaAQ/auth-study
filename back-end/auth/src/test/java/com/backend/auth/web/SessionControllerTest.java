package com.backend.auth.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.backend.auth.BaseIntegrationTest;
import com.backend.auth.repo.SessionRepository;

import jakarta.servlet.http.Cookie;

class SessionControllerTest extends BaseIntegrationTest {

    @Autowired
    private SessionRepository sessionRepository;

    // --- 1. Login & Logout Basics ---

    @Test
    @DisplayName("Login with valid credentials returns 200 and sets SessionToken cookie")
    void login_validCredentials_setsCookie() throws Exception {
        registerAndGetCookie("alice@example.com", "Password123!");

        Cookie loginCookie = loginAndGetCookie("alice@example.com", "Password123!");

        mockMvc.perform(get("/api/me/profile").cookie(loginCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("Login with wrong password returns 401 and sets no cookie")
    void login_wrongPassword_returnsUnauthorized() throws Exception {
        registerAndGetCookie("alice@example.com", "Password123!");

        mockMvc.perform(post("/api/auth/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "alice@example.com", "password": "WrongPassword!"}
                """))
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().doesNotExist("SessionToken"));
    }

    @Test
    @DisplayName("Logout clears cookie and invalidates subsequent requests")
    void logout_validSession_invalidatesCookie() throws Exception {
        Cookie cookie = registerAndGetCookie("alice@example.com", "Password123!");

        mockMvc.perform(delete("/api/auth/sessions/current").cookie(cookie))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("SessionToken", 0));

        mockMvc.perform(get("/api/me/profile").cookie(cookie))
                .andExpect(status().isUnauthorized());
    }

    // --- 2. Multi-Device & Session Revocation ---

    @Test
    @DisplayName("Revoke all sessions logs out every active device for the user")
    void deleteAllSessions_revokesAllDevices() throws Exception {
        Cookie device1 = registerAndGetCookie("bob@example.com", "Password123!");
        Cookie device2 = loginAndGetCookie("bob@example.com", "Password123!");

        mockMvc.perform(get("/api/me/profile").cookie(device1)).andExpect(status().isOk());
        mockMvc.perform(get("/api/me/profile").cookie(device2)).andExpect(status().isOk());

        mockMvc.perform(delete("/api/auth/sessions").cookie(device1))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("SessionToken", 0));

        mockMvc.perform(get("/api/me/profile").cookie(device1)).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/me/profile").cookie(device2)).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Revoke specific session by ID deletes only that session and handles current session clearance")
    void deleteSpecificSession_revokesOnlyTargetSession() throws Exception {
        Cookie session1 = registerAndGetCookie("charlie@example.com", "Password123!");
        Cookie session2 = loginAndGetCookie("charlie@example.com", "Password123!");

        UUID session1Id = sessionIdFor(session1);

        // Delete session1 using session2's cookie
        mockMvc.perform(delete("/api/auth/sessions/" + session1Id).cookie(session2))
                .andExpect(status().isNoContent());

        // Session 1 is dead, but Session 2 should still work
        mockMvc.perform(get("/api/me/profile").cookie(session1)).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/me/profile").cookie(session2)).andExpect(status().isOk());
    }

    // --- 3. Security & Authorization Checks ---

    @Test
    @DisplayName("Regular user cannot access admin session listing endpoint")
    void getSessions_regularUser_forbidden() throws Exception {
        Cookie userCookie = registerAndGetCookie("user@example.com", "Password123!");

        mockMvc.perform(get("/api/auth/sessions").cookie(userCookie))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User cannot delete another user's session ID (IDOR Protection)")
    void deleteSession_idorProtection_forbidden() throws Exception {
        Cookie aliceCookie = registerAndGetCookie("alice@example.com", "Password123!");
        Cookie bobCookie = registerAndGetCookie("bob@example.com", "Password123!");

        UUID bobsSessionId = sessionIdFor(bobCookie);

        // Alice attempts to delete Bob's session ID using her own cookie
        mockMvc.perform(delete("/api/auth/sessions/" + bobsSessionId).cookie(aliceCookie))
                .andExpect(status().isForbidden());

        // Bob's session must still be completely active and working
        mockMvc.perform(get("/api/me/profile").cookie(bobCookie))
                .andExpect(status().isOk());
    }

    // --- Helpers ---

    private UUID sessionIdFor(Cookie sessionCookie) {
        return sessionRepository.findBySessionToken(sessionCookie.getValue())
                .orElseThrow(() -> new AssertionError("No session found for cookie value"))
                .getSessionId();
    }
}