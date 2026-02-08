package org.example.eliteback.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_SCOPE = "scope";
    public static final String SCOPE_ACCESS = "access";
    public static final String SCOPE_REFRESH = "refresh";
    public static final String SCOPE_ONBOARDING = "onboarding";

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtUtil(JwtProperties properties) {
        this.properties = properties;
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            keyBytes = padded;
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long userId, String email, List<String> roles) {
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLES, roles)
                .claim(CLAIM_SCOPE, SCOPE_ACCESS)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + properties.getAccessExpirationMs()))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Long userId, String email) {
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_SCOPE, SCOPE_REFRESH)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + properties.getRefreshExpirationMs()))
                .signWith(key)
                .compact();
    }

    public String generateOnboardingToken(Long userId, String email) {
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_SCOPE, SCOPE_ONBOARDING)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + properties.getOnboardingExpirationMs()))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isOnboardingToken(Claims claims) {
        return SCOPE_ONBOARDING.equals(claims.get(CLAIM_SCOPE, String.class));
    }

    public boolean isRefreshToken(Claims claims) {
        return SCOPE_REFRESH.equals(claims.get(CLAIM_SCOPE, String.class));
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(Claims claims) {
        List<?> list = claims.get(CLAIM_ROLES, List.class);
        if (list == null) return List.of();
        return list.stream().map(Object::toString).collect(Collectors.toList());
    }

    public Long getUserId(Claims claims) {
        Number n = claims.get(CLAIM_USER_ID, Number.class);
        return n == null ? null : n.longValue();
    }

    public String getEmail(Claims claims) {
        return claims.get(CLAIM_EMAIL, String.class);
    }
}
