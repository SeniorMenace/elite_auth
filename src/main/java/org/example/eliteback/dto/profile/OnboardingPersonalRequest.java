package org.example.eliteback.dto.profile;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class OnboardingPersonalRequest {
    @Size(max = 500)
    private String bio;

    private LocalDate dateOfBirth;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String country;

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
