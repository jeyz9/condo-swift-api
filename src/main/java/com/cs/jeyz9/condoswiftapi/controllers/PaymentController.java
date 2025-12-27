package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.dto.CheckoutRequest;
import com.cs.jeyz9.condoswiftapi.dto.CheckoutResponse;
import com.cs.jeyz9.condoswiftapi.services.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @PostMapping("/checkout")
    public CheckoutResponse checkout(@RequestBody CheckoutRequest request, Principal principal) throws StripeException {
        if (principal == null) {
            throw new RuntimeException("Unauthenticated");
        }
        return paymentService.createCheckoutSession(request.getPackageId(), principal.getName());
    }
}
