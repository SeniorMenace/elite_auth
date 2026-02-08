package org.example.eliteback.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class RefreshRequest {
    @NotBlank(message = "refresh_token is required")
    private String refresh_token;

    public String getRefresh_token() { return refresh_token; }
    public void setRefresh_token(String refresh_token) { this.refresh_token = refresh_token; }
}
