package com.cs.jeyz9.condoswiftapi.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class StripeConfig {
    public StripeConfig(@Value("${stripe.secret-key:}") String secretKey) {
        if (secretKey == null || secretKey.isBlank()) {
            return;
        }
        Stripe.apiKey = secretKey;
    }
}
