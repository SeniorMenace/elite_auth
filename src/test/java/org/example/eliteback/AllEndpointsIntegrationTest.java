package org.example.eliteback;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class AllEndpointsIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /health returns 200")
    void healthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/auth/signup returns 4xx/2xx")
    void signupEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/v1/auth/verify-email returns 4xx/2xx")
    void verifyEmailEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/auth/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login returns 4xx/2xx")
    void loginEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh returns 4xx/2xx")
    void refreshEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("PATCH /api/v1/profile/onboarding/gender returns 4xx/2xx")
    void patchGender() throws Exception {
        mockMvc.perform(patch("/api/v1/profile/onboarding/gender")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("PATCH /api/v1/profile/onboarding/personal returns 4xx/2xx")
    void patchPersonal() throws Exception {
        mockMvc.perform(patch("/api/v1/profile/onboarding/personal")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("PATCH /api/v1/profile/onboarding/character returns 4xx/2xx")
    void patchCharacter() throws Exception {
        mockMvc.perform(patch("/api/v1/profile/onboarding/character")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/v1/profile/photos returns 4xx/2xx")
    void postPhotos() throws Exception {
        mockMvc.perform(post("/api/v1/profile/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/v1/subscription/create returns 4xx/2xx")
    void postSubscriptionCreate() throws Exception {
        mockMvc.perform(post("/api/v1/subscription/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }
}
