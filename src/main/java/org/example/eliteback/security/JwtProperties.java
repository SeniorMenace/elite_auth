package org.example.eliteback.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;
    private long accessExpirationMs = TimeUnit.MINUTES.toMillis(15);
    private long refreshExpirationMs = TimeUnit.DAYS.toMillis(7);
    private long onboardingExpirationMs = TimeUnit.MINUTES.toMillis(30);

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public long getAccessExpirationMs() { return accessExpirationMs; }
    public void setAccessExpirationMs(long accessExpirationMs) { this.accessExpirationMs = accessExpirationMs; }
    public long getRefreshExpirationMs() { return refreshExpirationMs; }
    public void setRefreshExpirationMs(long refreshExpirationMs) { this.refreshExpirationMs = refreshExpirationMs; }
    public long getOnboardingExpirationMs() { return onboardingExpirationMs; }
    public void setOnboardingExpirationMs(long onboardingExpirationMs) { this.onboardingExpirationMs = onboardingExpirationMs; }
}
