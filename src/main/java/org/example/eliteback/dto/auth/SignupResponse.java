package org.example.eliteback.dto.auth;

public class SignupResponse {
    private String message;
    private String onboarding_token;
    private Long userId;
    private String nextStep;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getOnboarding_token() { return onboarding_token; }
    public void setOnboarding_token(String onboarding_token) { this.onboarding_token = onboarding_token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNextStep() { return nextStep; }
    public void setNextStep(String nextStep) { this.nextStep = nextStep; }
}
