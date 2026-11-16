package com.backend.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;



import jakarta.servlet.http.Cookie;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetDatabase() {
        jdbcTemplate.execute("""
            DO $$
            DECLARE
                tables TEXT;
            BEGIN
                SELECT string_agg(format('%I.%I', schemaname, tablename), ', ')
                INTO tables
                FROM pg_tables
                WHERE schemaname = 'public'
                  AND tablename NOT IN ('flyway_schema_history', 'databasechangelog', 'databasechangeloglock');

                IF tables IS NOT NULL THEN
                    EXECUTE 'TRUNCATE TABLE ' || tables || ' RESTART IDENTITY CASCADE';
                END IF;
            END $$;
        """);
    }

    // --- Reusable Auth Helpers ---

    protected Cookie registerAndGetCookie(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "%s", "password": "%s"}
                """.formatted(email, password)))
                .andExpect(status().isCreated())
                .andExpect(cookie().exists("SessionToken"))
                .andReturn();

        return result.getResponse().getCookie("SessionToken");
    }

    protected Cookie loginAndGetCookie(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "%s", "password": "%s"}
                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("SessionToken"))
                .andReturn();

        return result.getResponse().getCookie("SessionToken");
    }
}