package org.example.eliteback.controller;

import jakarta.validation.Valid;
import org.example.eliteback.dto.ApiResponse;
import org.example.eliteback.dto.profile.*;
import org.example.eliteback.security.UserPrincipal;
import org.example.eliteback.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/onboarding/gender")
    public ResponseEntity<ApiResponse<OnboardingStepResponse>> updateGender(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody OnboardingGenderRequest request) {
        OnboardingStepResponse data = userService.updateGender(principal.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.of(data));
    }

    @PatchMapping("/onboarding/personal")
    public ResponseEntity<ApiResponse<OnboardingStepResponse>> updatePersonal(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody OnboardingPersonalRequest request) {
        OnboardingStepResponse data = userService.updatePersonal(principal.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.of(data));
    }

    @PatchMapping("/onboarding/character")
    public ResponseEntity<ApiResponse<OnboardingStepResponse>> updateCharacter(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody OnboardingCharacterRequest request) {
        OnboardingStepResponse data = userService.updateCharacter(principal.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.of(data));
    }

    @PostMapping("/photos")
    public ResponseEntity<ApiResponse<PhotoUploadResponse>> uploadPhotos(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("files") MultipartFile[] files) {
        PhotoUploadResponse data = userService.uploadPhotos(principal.getUserId(), files);
        return ResponseEntity.ok(ApiResponse.of(data));
    }
}
