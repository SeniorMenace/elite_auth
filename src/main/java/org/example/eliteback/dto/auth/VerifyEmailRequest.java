package org.example.eliteback.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VerifyEmailRequest {
    @NotBlank(message = "email is required")
    @Email
    private String email;

    @NotBlank(message = "otp is required")
    @Size(min = 6, max = 6)
    private String otp;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
