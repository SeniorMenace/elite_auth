package org.example.eliteback.dto.auth;

public class VerifyEmailResponse {
    private String message;
    private String access_token;
    private String refresh_token;
    private String nextStep;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getAccess_token() { return access_token; }
    public void setAccess_token(String access_token) { this.access_token = access_token; }
    public String getRefresh_token() { return refresh_token; }
    public void setRefresh_token(String refresh_token) { this.refresh_token = refresh_token; }
    public String getNextStep() { return nextStep; }
    public void setNextStep(String nextStep) { this.nextStep = nextStep; }

}
