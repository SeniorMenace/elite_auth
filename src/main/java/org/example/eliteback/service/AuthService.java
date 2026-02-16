package org.example.eliteback.service;

import org.example.eliteback.dto.auth.*;
import org.example.eliteback.entity.RefreshToken;
import org.example.eliteback.entity.Subscription;
import org.example.eliteback.entity.User;
import org.example.eliteback.repository.RefreshTokenRepository;
import org.example.eliteback.repository.SubscriptionRepository;
import org.example.eliteback.repository.UserRepository;
import org.example.eliteback.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final EmailService emailService;
    private final TelegramOtpService telegramOtpService;

    private static final String ROLE_USER = "ROLE_USER";
    private static final String NEXT_STEP_VERIFY = "verify_email";
    private static final String NEXT_STEP_ONBOARDING = "onboarding";

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
            SubscriptionRepository subscriptionRepository, PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil, OtpService otpService, EmailService emailService,
            TelegramOtpService telegramOtpService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.otpService = otpService;
        this.emailService = emailService;
        this.telegramOtpService = telegramOtpService;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false);
        user.setActive(false);
        user.setRegistrationStep(0);
        user.setRoles(java.util.Set.of(ROLE_USER));
        if (request.getTelegramId() != null && !request.getTelegramId().isBlank()) {
            user.setTelegramId(request.getTelegramId());
        }
        user = userRepository.save(user);
        String otp = otpService.generateAndStore(user.getEmail());
        if (user.getTelegramId() != null && !user.getTelegramId().isBlank()) {
            telegramOtpService.sendOtpTelegram(user.getTelegramId(), otp);
        } else {
            emailService.sendOtpEmail(user.getEmail(), otp);
        }
        String onboardingToken = jwtUtil.generateOnboardingToken(user.getId(), user.getEmail());
        SignupResponse resp = new SignupResponse();
        resp.setMessage("Verification code sent to your email or Telegram");
        resp.setOnboarding_token(onboardingToken);
        resp.setUserId(user.getId());
        resp.setNextStep(NEXT_STEP_VERIFY);
        return resp;
    }

    @Transactional
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest request) {
        if (!otpService.validate(request.getEmail(), request.getOtp())) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEmailVerified(true);
        userRepository.save(user);
        List<String> roles = List.copyOf(user.getRoles());
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
        persistRefreshToken(user, refreshTokenValue);
        VerifyEmailResponse resp = new VerifyEmailResponse();
        resp.setMessage("Email verified successfully");
        resp.setAccess_token(accessToken);
        resp.setRefresh_token(refreshTokenValue);
        resp.setNextStep(NEXT_STEP_ONBOARDING);
        return resp;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new IllegalArgumentException("Email not verified");
        }
        Optional<Subscription> sub = subscriptionRepository.findByUserId(user.getId());
        boolean active = sub.map(s -> "ACTIVE".equals(s.getStatus())).orElse(false);
        if (!active) {
            throw new IllegalArgumentException("Subscription is not active");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        if (!Boolean.TRUE.equals(user.getActive())) {
            user.setActive(true);
            userRepository.save(user);
        }
        List<String> roles = List.copyOf(user.getRoles());
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
        persistRefreshToken(user, refreshTokenValue);
        LoginResponse resp = new LoginResponse();
        resp.setMessage("Login successful");
        resp.setAccess_token(accessToken);
        resp.setRefresh_token(refreshTokenValue);
        return resp;
    }

    @Transactional
    public RefreshResponse refresh(RefreshRequest request) {
        String tokenValue = request.getRefresh_token();
        io.jsonwebtoken.Claims claims;
        try {
            claims = jwtUtil.parseToken(tokenValue);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        if (!jwtUtil.isRefreshToken(claims)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String tokenHash = hashToken(tokenValue);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found or revoked"));
        if (Boolean.TRUE.equals(stored.getRevoked()) || stored.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            throw new IllegalArgumentException("Refresh token expired or revoked");
        }
        refreshTokenRepository.delete(stored);
        Long userId = jwtUtil.getUserId(claims);
        String email = jwtUtil.getEmail(claims);
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<String> roles = List.copyOf(user.getRoles());
        String newAccessToken = jwtUtil.generateAccessToken(userId, email, roles);
        String newRefreshTokenValue = jwtUtil.generateRefreshToken(userId, email);
        persistRefreshToken(user, newRefreshTokenValue);
        RefreshResponse resp = new RefreshResponse();
        resp.setAccess_token(newAccessToken);
        resp.setRefresh_token(newRefreshTokenValue);
        return resp;
    }

    private void persistRefreshToken(User user, String tokenValue) {
        String tokenHash = hashToken(tokenValue);
        Instant expiresAt = Instant.now().plusSeconds(604800L);
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(tokenHash);
        rt.setExpiresAt(expiresAt);
        rt.setRevoked(false);
        refreshTokenRepository.save(rt);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            return UUID.nameUUIDFromBytes(token.getBytes()).toString();
        }
    }
}
