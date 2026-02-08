package org.example.eliteback.controller;

import jakarta.validation.Valid;
import org.example.eliteback.dto.ApiResponse;
import org.example.eliteback.dto.subscription.CreateSubscriptionRequest;
import org.example.eliteback.dto.subscription.CreateSubscriptionResponse;
import org.example.eliteback.security.UserPrincipal;
import org.example.eliteback.service.StripeSubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {

    private final StripeSubscriptionService stripeSubscriptionService;

    public SubscriptionController(StripeSubscriptionService stripeSubscriptionService) {
        this.stripeSubscriptionService = stripeSubscriptionService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateSubscriptionResponse>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody(required = false) CreateSubscriptionRequest request) {
        String paymentMethodId = request != null ? request.getPaymentMethodId() : null;
        CreateSubscriptionResponse data = stripeSubscriptionService.createSubscription(principal.getUserId(), paymentMethodId);
        return ResponseEntity.ok(ApiResponse.of(data));
    }
}
