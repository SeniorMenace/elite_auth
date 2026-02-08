package org.example.eliteback.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private static final String OTP_PREFIX = "otp:";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final RedisTemplate<String, String> redisTemplate;

    public OtpService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Value("${app.otp.ttl-minutes:10}")
    private int ttlMinutes;

    @Value("${app.otp.length:6}")
    private int otpLength;

    public String generateAndStore(String email) {
        String otp = generateNumericOtp(otpLength);
        String key = OTP_PREFIX + email.toLowerCase();
        redisTemplate.opsForValue().set(key, otp, ttlMinutes, TimeUnit.MINUTES);
        return otp;
    }

    public boolean validate(String email, String otp) {
        if (email == null || otp == null) return false;
        String key = OTP_PREFIX + email.toLowerCase();
        String stored = redisTemplate.opsForValue().get(key);
        boolean valid = otp.equals(stored);
        if (valid) {
            redisTemplate.delete(key);
        }
        return valid;
    }

    private String generateNumericOtp(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
