package org.example.eliteback.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {
    @NotBlank(message = "fullName is required")
    @Size(max = 255)
    private String fullName;

    @NotBlank(message = "username is required")
    @Size(max = 100)
    private String username;

    @NotBlank(message = "email is required")
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 100)
    private String password;

    // Optional: Telegram chat ID for OTP delivery
    private String telegramId;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }
}
