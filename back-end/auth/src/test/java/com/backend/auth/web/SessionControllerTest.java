package com.backend.auth.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.backend.auth.BaseIntegrationTest;
import com.backend.auth.model.User;
import com.backend.auth.model.UserRole;
import com.jayway.jsonpath.JsonPath;

import jakarta.servlet.http.Cookie;

/**
 * Session endpoints, grouped by endpoint. Within each group the order is:
 * happy path -> unauthenticated -> validation/edge cases -> security/isolation.
 *
 * Assumes BaseIntegrationTest provides: mockMvc, sessionRepository,
 * registerAndGetCookie(email, pw), loginAndGetCookie(email, pw).
 */
class SessionControllerTest extends BaseIntegrationTest {

    // =====================================================================
    // POST /api/auth/sessions  (login)
    // =====================================================================
    @Nested
    @DisplayName("POST /api/auth/sessions (login)")
    class Login {

        // --- Happy path ---

        @Test
        @DisplayName("Valid credentials return 200 and a working SessionToken cookie")
        void validCredentials_setsWorkingCookie() throws Exception {
            registerAndGetCookie("alice@example.com", "Password123!");

            Cookie loginCookie = loginAndGetCookie("alice@example.com", "Password123!");

            mockMvc.perform(get("/api/me/profile").cookie(loginCookie))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("alice@example.com"));
        }

        @Test
        @DisplayName("Cookie is HttpOnly and scoped to path /")
        void cookieAttributes() throws Exception {
            registerAndGetCookie("alice@example.com", "Password123!");

            mockMvc.perform(post("/api/auth/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"email": "alice@example.com", "password": "Password123!"}
                    """))
                    .andExpect(status().isOk())
                    .andExpect(cookie().exists("SessionToken"))
                    .andExpect(cookie().httpOnly("SessionToken", true))
                    .andExpect(cookie().path("SessionToken", "/"));
            // Secure flag is usually off under MockMvc/http; assert it only if your test config sets it.
        }

        @Test
        @DisplayName("Each login issues a distinct session token")
        void issuesUniqueTokens() throws Exception {
            Cookie first = registerAndGetCookie("alice@example.com", "Password123!");
            Cookie second = loginAndGetCookie("alice@example.com", "Password123!");

            assertNotEquals(first.getValue(), second.getValue());
        }

        // --- Bad credentials ---

        @Test
        @DisplayName("Wrong password returns 401 and sets no cookie")
        void wrongPassword_unauthorized() throws Exception {
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
        @DisplayName("Unknown email returns 401 and sets no cookie")
        void unknownEmail_unauthorized() throws Exception {
            mockMvc.perform(post("/api/auth/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"email": "ghost@example.com", "password": "Password123!"}
                    """))
                    .andExpect(status().isUnauthorized())
                    .andExpect(cookie().doesNotExist("SessionToken"));
        }

        // --- Validation ---

        @Test
        @DisplayName("Missing password returns 400")
        void missingPassword_badRequest() throws Exception {
            mockMvc.perform(post("/api/auth/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"email": "alice@example.com"}
                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(cookie().doesNotExist("SessionToken"));
        }

        @Test
        @DisplayName("Empty JSON body returns 400")
        void emptyBody_badRequest() throws Exception {
            mockMvc.perform(post("/api/auth/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Malformed JSON returns 400")
        void malformedJson_badRequest() throws Exception {
            mockMvc.perform(post("/api/auth/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{not json"))
                    .andExpect(status().isBadRequest());
        }

        // --- Security ---

        @Test
        @DisplayName("Unknown email and wrong password return identical bodies (no user enumeration)")
        void noUserEnumeration() throws Exception {
            registerAndGetCookie("alice@example.com", "Password123!");

            MvcResult wrongPassword = mockMvc.perform(post("/api/auth/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"email": "alice@example.com", "password": "WrongPassword!"}
                    """))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            MvcResult unknownEmail = mockMvc.perform(post("/api/auth/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"email": "ghost@example.com", "password": "WrongPassword!"}
                    """))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            assertEquals(
                    wrongPassword.getResponse().getContentAsString(),
                    unknownEmail.getResponse().getContentAsString());
        }
    }

    // =====================================================================
    // DELETE /api/auth/sessions/current  (logout)
    // =====================================================================
    @Nested
    @DisplayName("DELETE /api/auth/sessions/current (logout)")
    class Logout {

        // --- Happy path ---

        @Test
        @DisplayName("Valid session returns 204, clears cookie, and invalidates it server-side")
        void validSession_invalidatesCookie() throws Exception {
            Cookie cookie = registerAndGetCookie("alice@example.com", "Password123!");

            mockMvc.perform(delete("/api/auth/sessions/current").cookie(cookie))
                    .andExpect(status().isNoContent())
                    .andExpect(cookie().maxAge("SessionToken", 0));

            mockMvc.perform(get("/api/me/profile").cookie(cookie))
                    .andExpect(status().isUnauthorized());
        }

        // --- Unauthenticated ---

        @Test
        @DisplayName("No cookie returns 401")
        void noCookie_unauthorized() throws Exception {
            mockMvc.perform(delete("/api/auth/sessions/current"))
                    .andExpect(status().isUnauthorized());
        }

        // --- Edge cases ---

        @Test
        @DisplayName("Logging out twice with the same cookie returns 401 the second time")
        void twice_secondIsUnauthorized() throws Exception {
            Cookie cookie = registerAndGetCookie("alice@example.com", "Password123!");

            mockMvc.perform(delete("/api/auth/sessions/current").cookie(cookie))
                    .andExpect(status().isNoContent());

            mockMvc.perform(delete("/api/auth/sessions/current").cookie(cookie))
                    .andExpect(status().isUnauthorized());
        }

        // --- Isolation ---

        @Test
        @DisplayName("Only ends the current session; other devices keep working")
        void leavesOtherSessionsAlive() throws Exception {
            Cookie device1 = registerAndGetCookie("bob@example.com", "Password123!");
            Cookie device2 = loginAndGetCookie("bob@example.com", "Password123!");

            mockMvc.perform(delete("/api/auth/sessions/current").cookie(device1))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/me/profile").cookie(device1)).andExpect(status().isUnauthorized());
            mockMvc.perform(get("/api/me/profile").cookie(device2)).andExpect(status().isOk());
        }
    }

    // =====================================================================
    // DELETE /api/auth/sessions  (revoke all)
    // =====================================================================
    @Nested
    @DisplayName("DELETE /api/auth/sessions (revoke all)")
    class RevokeAll {

        // --- Happy path ---

        @Test
        @DisplayName("Logs out every active device for the user and clears the cookie")
        void revokesAllDevices() throws Exception {
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

        // --- Unauthenticated ---

        @Test
        @DisplayName("No cookie returns 401")
        void noCookie_unauthorized() throws Exception {
            mockMvc.perform(delete("/api/auth/sessions"))
                    .andExpect(status().isUnauthorized());
        }

        // --- Isolation ---

        @Test
        @DisplayName("Only affects the caller's sessions, not other users'")
        void doesNotAffectOtherUsers() throws Exception {
            Cookie alice = registerAndGetCookie("alice@example.com", "Password123!");
            Cookie bob = registerAndGetCookie("bob@example.com", "Password123!");

            mockMvc.perform(delete("/api/auth/sessions").cookie(alice))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/me/profile").cookie(alice)).andExpect(status().isUnauthorized());
            mockMvc.perform(get("/api/me/profile").cookie(bob)).andExpect(status().isOk());
        }
    }

    // =====================================================================
    // DELETE /api/auth/sessions/{sessionId}  (revoke by ID)
    // =====================================================================
    @Nested
    @DisplayName("DELETE /api/auth/sessions/{sessionId} (revoke by ID)")
    class RevokeById {

        // --- Happy path ---

        @Test
        @DisplayName("Deletes only the target session; the caller's own session keeps working")
        void revokesOnlyTargetSession() throws Exception {
            Cookie session1 = registerAndGetCookie("charlie@example.com", "Password123!");
            Cookie session2 = loginAndGetCookie("charlie@example.com", "Password123!");

            // Delete session1 using session2's cookie
            mockMvc.perform(delete("/api/auth/sessions/" + sessionIdFor(session1)).cookie(session2))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/me/profile").cookie(session1)).andExpect(status().isUnauthorized());
            mockMvc.perform(get("/api/me/profile").cookie(session2)).andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deleting a non-current session does not clear the caller's cookie")
        void otherOwnSession_doesNotClearCookie() throws Exception {
            Cookie session1 = registerAndGetCookie("alice@example.com", "Password123!");
            Cookie session2 = loginAndGetCookie("alice@example.com", "Password123!");

            mockMvc.perform(delete("/api/auth/sessions/" + sessionIdFor(session1)).cookie(session2))
                    .andExpect(status().isNoContent())
                    .andExpect(cookie().doesNotExist("SessionToken"));
        }

        @Test
        @DisplayName("Deleting the current session by ID returns 204 and clears the cookie")
        void currentSession_clearsCookie() throws Exception {
            Cookie cookie = registerAndGetCookie("alice@example.com", "Password123!");

            mockMvc.perform(delete("/api/auth/sessions/" + sessionIdFor(cookie)).cookie(cookie))
                    .andExpect(status().isNoContent())
                    .andExpect(cookie().maxAge("SessionToken", 0));

            mockMvc.perform(get("/api/me/profile").cookie(cookie))
                    .andExpect(status().isUnauthorized());
        }

        // --- Unauthenticated ---

        @Test
        @DisplayName("No cookie returns 401")
        void noCookie_unauthorized() throws Exception {
            mockMvc.perform(delete("/api/auth/sessions/" + UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }

        // --- Validation / edge cases ---

        @Test
        @DisplayName("Nonexistent session ID returns 404")
        void nonexistentId_notFound() throws Exception {
            Cookie cookie = registerAndGetCookie("alice@example.com", "Password123!");

            mockMvc.perform(delete("/api/auth/sessions/" + UUID.randomUUID()).cookie(cookie))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Malformed session ID returns 400")
        void malformedId_badRequest() throws Exception {
            Cookie cookie = registerAndGetCookie("alice@example.com", "Password123!");

            mockMvc.perform(delete("/api/auth/sessions/not-a-uuid").cookie(cookie))
                    .andExpect(status().isBadRequest());
        }

        // --- Security ---

        @Test
        @DisplayName("Another user's session returns 404 (indistinguishable from nonexistent) and stays intact")
        void idorProtection_notFound() throws Exception {
            Cookie aliceCookie = registerAndGetCookie("alice@example.com", "Password123!");
            Cookie bobCookie = registerAndGetCookie("bob@example.com", "Password123!");

            mockMvc.perform(delete("/api/auth/sessions/" + sessionIdFor(bobCookie)).cookie(aliceCookie))
                    .andExpect(status().isNotFound());

            // Bob's session must still be completely active and working
            mockMvc.perform(get("/api/me/profile").cookie(bobCookie))
                    .andExpect(status().isOk());
        }
    }

    // =====================================================================
    // GET /api/auth/sessions  (admin listing)
    // =====================================================================
    @Nested
    @DisplayName("GET /api/auth/sessions (admin listing)")
    class ListSessions {

        // --- Happy path ---

		/*
		 * TODO FIX
		 * @Test
		 * 
		 * @DisplayName("Admin gets 200 and the response does not leak raw tokens") void
		 * admin_ok() throws Exception { Cookie admin = adminCookie();
		 * 
		 * MvcResult result = mockMvc.perform(get("/api/auth/sessions").cookie(admin))
		 * .andExpect(status().isOk()) .andExpect(jsonPath("$").isArray()) .andReturn();
		 * 
		 * assertFalse(
		 * result.getResponse().getContentAsString().contains(admin.getValue()),
		 * "Session listing must not expose raw session tokens"); }
		 */

        // --- Unauthenticated ---

        @Test
        @DisplayName("No cookie returns 401")
        void noCookie_unauthorized() throws Exception {
            mockMvc.perform(get("/api/auth/sessions"))
                    .andExpect(status().isUnauthorized());
        }

        // --- Authorization ---

        @Test
        @DisplayName("Regular user gets 403")
        void regularUser_forbidden() throws Exception {
            Cookie userCookie = registerAndGetCookie("user@example.com", "Password123!");

            mockMvc.perform(get("/api/auth/sessions").cookie(userCookie))
                    .andExpect(status().isForbidden());
        }
    }

    // =====================================================================
    // Cross-cutting: how the session cookie itself is validated
    // =====================================================================
    @Nested
    @DisplayName("Session cookie validation")
    class CookieValidation {

        @Test
        @DisplayName("Garbage cookie value is rejected with 401")
        void garbageCookie_unauthorized() throws Exception {
            Cookie fake = new Cookie("SessionToken", "not-a-real-token");

            mockMvc.perform(get("/api/me/profile").cookie(fake))
                    .andExpect(status().isUnauthorized());
        }
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    private UUID sessionIdFor(Cookie sessionCookie) {
        return sessionRepository.findBySessionToken(sessionCookie.getValue())
                .orElseThrow(() -> new AssertionError("No session found for cookie value"))
                .getSessionId();
    }

    private Cookie adminCookie() throws Exception {
        String email = "admin@example.com";
        String password = "Password123!";

        Cookie cookie = registerAndGetCookie(email, password);

        // Get the new user's ID from the profile endpoint
        String body = mockMvc.perform(get("/api/me/profile").cookie(cookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UUID userId = UUID.fromString(JsonPath.read(body, "$.userId"));

        User admin = userRepository.findById(userId).orElseThrow();
        admin.setRole(UserRole.ADMIN);          
        userRepository.save(admin);

        // Fresh login so the new session reflects the ADMIN role
        return loginAndGetCookie(email, password);
    }
}