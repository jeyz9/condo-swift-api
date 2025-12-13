package com.cs.jeyz9.condoswiftapi.controllers;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stripe")
public class CheckoutController {
    
    @Value("${stripe.domain}")
    private String domain;
    
    @Value("${stripe.price.id}")
    private String priceId;

    @PostMapping("/create-checkout-session")
    public void createCheckoutSession(HttpServletResponse response) throws Exception {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(domain + "?success=true")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(priceId)
                                .setQuantity(1L)
                                .build()
                ).build();
        Session session = Session.create(params);
        
        response.setStatus(303);
        response.sendRedirect(session.getUrl());
    }
}
