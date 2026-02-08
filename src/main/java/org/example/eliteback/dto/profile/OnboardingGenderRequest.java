package org.example.eliteback.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OnboardingGenderRequest {
    @NotBlank(message = "gender is required")
    @Size(max = 50)
    private String gender;

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
