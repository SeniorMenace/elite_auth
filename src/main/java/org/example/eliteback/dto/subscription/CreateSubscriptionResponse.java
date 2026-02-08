package org.example.eliteback.dto.subscription;

public class CreateSubscriptionResponse {
    private String clientSecret;
    private String subscriptionId;

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }
}
