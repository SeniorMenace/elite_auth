package org.example.eliteback.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import org.example.eliteback.dto.subscription.CreateSubscriptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.eliteback.entity.User;
import org.example.eliteback.entity.Subscription;
import org.example.eliteback.repository.SubscriptionRepository;
import org.example.eliteback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.Instant;

@Service
public class StripeSubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(StripeSubscriptionService.class);

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.price.id}")
    private String priceId;

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public StripeSubscriptionService(SubscriptionRepository subscriptionRepository,
                                      UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        if (stripeApiKey != null && !stripeApiKey.isBlank()) {
            Stripe.apiKey = stripeApiKey;
        }
    }

    @Transactional
    public CreateSubscriptionResponse createSubscription(Long userId, String paymentMethodId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (priceId == null || priceId.isBlank()) {
            throw new IllegalStateException("Stripe price is not configured");
        }
        try {
            String customerId = subscriptionRepository.findByUserId(userId)
                    .map(Subscription::getStripeCustomerId)
                    .orElse(null);
            if (customerId == null) {
                Customer customer = Customer.create(CustomerCreateParams.builder()
                        .setEmail(user.getEmail())
                        .setName(user.getFullName())
                        .build());
                customerId = customer.getId();
            }
            SubscriptionCreateParams.Builder paramsBuilder = SubscriptionCreateParams.builder()
                    .setCustomer(customerId)
                    .addItem(SubscriptionCreateParams.Item.builder().setPrice(priceId).build())
                    .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                    .setPaymentSettings(SubscriptionCreateParams.PaymentSettings.builder()
                            .setSaveDefaultPaymentMethod(SubscriptionCreateParams.PaymentSettings.SaveDefaultPaymentMethod.ON_SUBSCRIPTION)
                            .build());
            if (paymentMethodId != null && !paymentMethodId.isBlank()) {
                paramsBuilder.setDefaultPaymentMethod(paymentMethodId);
            }
            com.stripe.model.Subscription stripeSubscription = com.stripe.model.Subscription.create(paramsBuilder.build());
            String clientSecret = null;
            try {
                java.util.Map<String, Object> expandParams = new java.util.HashMap<>();
                expandParams.put("expand[]", java.util.List.of("latest_invoice.payment_intent"));
                com.stripe.model.Subscription retrieved = com.stripe.model.Subscription.retrieve(
                        stripeSubscription.getId(),
                        expandParams,
                        null
                );
                if (retrieved.getLatestInvoiceObject() != null
                        && retrieved.getLatestInvoiceObject().getPaymentIntentObject() != null) {
                    clientSecret = retrieved.getLatestInvoiceObject().getPaymentIntentObject().getClientSecret();
                }
            } catch (Exception e) {
                log.debug("Could not retrieve payment intent client secret: {}", e.getMessage());
            }
            Subscription entity = subscriptionRepository.findByUserId(userId).orElse(null);
            if (entity == null) {
                entity = new Subscription();
                entity.setUser(user);
                entity.setStripeCustomerId(customerId);
                entity.setStripeSubscriptionId(stripeSubscription.getId());
                entity.setStatus(stripeSubscription.getStatus());
                if (stripeSubscription.getCurrentPeriodEnd() != null) {
                    entity.setCurrentPeriodEnd(Instant.ofEpochSecond(stripeSubscription.getCurrentPeriodEnd()));
                }
                subscriptionRepository.save(entity);
            } else {
                entity.setStripeSubscriptionId(stripeSubscription.getId());
                entity.setStatus(stripeSubscription.getStatus());
                if (stripeSubscription.getCurrentPeriodEnd() != null) {
                    entity.setCurrentPeriodEnd(Instant.ofEpochSecond(stripeSubscription.getCurrentPeriodEnd()));
                }
                subscriptionRepository.save(entity);
            }
            CreateSubscriptionResponse resp = new CreateSubscriptionResponse();
            resp.setClientSecret(clientSecret);
            resp.setSubscriptionId(stripeSubscription.getId());
            return resp;
        } catch (StripeException e) {
            log.error("Stripe error: {}", e.getMessage());
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }
    }
}
