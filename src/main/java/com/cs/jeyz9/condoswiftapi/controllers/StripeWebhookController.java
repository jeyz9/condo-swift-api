package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.services.StripeWebhookService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stripe")
public class StripeWebhookController {
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;
    
    private final StripeWebhookService stripeWebhookService;
    
    @Autowired
    public StripeWebhookController(StripeWebhookService stripeWebhookService) {
        this.stripeWebhookService = stripeWebhookService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sig) {

        try {
            Event event = Webhook.constructEvent(payload, sig, webhookSecret);

            if ("checkout.session.completed".equals(event.getType())) {

                JsonNode sessionJson = new ObjectMapper()
                        .readTree(event.getDataObjectDeserializer().getRawJson());

                String orderIdStr = sessionJson
                        .path("metadata")
                        .path("orderId")
                        .asText(null);

                if (orderIdStr != null) {
                    stripeWebhookService.handleCheckoutCompleted(
                            Long.valueOf(orderIdStr)
                    );
                }
            }

            return ResponseEntity.ok("success");

        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid signature");

        } catch (Exception e) {
            return ResponseEntity.ok("error handled");
        }
    }
}
