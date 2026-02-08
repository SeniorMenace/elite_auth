package org.example.eliteback.dto.profile;

import jakarta.validation.constraints.Size;

public class OnboardingCharacterRequest {
    @Size(max = 1000)
    private String characterTraits;

    public String getCharacterTraits() { return characterTraits; }
    public void setCharacterTraits(String characterTraits) { this.characterTraits = characterTraits; }
}
