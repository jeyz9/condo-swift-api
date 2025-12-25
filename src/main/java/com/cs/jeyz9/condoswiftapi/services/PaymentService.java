package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.CheckoutResponse;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Order;
import com.cs.jeyz9.condoswiftapi.models.OrderStatus;
import com.cs.jeyz9.condoswiftapi.models.Package;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.repository.OrderRepository;
import com.cs.jeyz9.condoswiftapi.repository.PackageRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {
    
    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;
    
    private final PackageRepository packageRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public PaymentService(PackageRepository packageRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.packageRepository = packageRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }
    
    @Transactional
    public CheckoutResponse createCheckoutSession(Long packageId, String email) throws StripeException {
        Package pkg = packageRepository.findById(packageId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Package not found."));

        User user = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));

        BigDecimal price = pkg.getPrice();
        long unitAmount = price
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();


        Order order = Order.builder()
                .orderNo(UUID.randomUUID())
                .user(user)
                .packages(pkg)
                .amount(unitAmount)
                .currency(pkg.getCurrency().toLowerCase())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        orderRepository.save(order);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(pkg.getCurrency().toLowerCase())
                                                .setUnitAmount(unitAmount)
                                                .setProductData(
                                                        SessionCreateParams
                                                                .LineItem
                                                                .PriceData
                                                                .ProductData
                                                                .builder()
                                                                .setName(pkg.getName())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("orderId", order.getId().toString())
                .build();

        Session session = Session.create(params);
        
        order.setStripeSessionId(session.getId());
        orderRepository.save(order);
        
        return new CheckoutResponse(session.getUrl());
    }
}
