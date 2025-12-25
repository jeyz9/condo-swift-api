package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.models.CreditTransaction;
import com.cs.jeyz9.condoswiftapi.models.Order;
import com.cs.jeyz9.condoswiftapi.models.OrderStatus;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.repository.CreditTransactionRepository;
import com.cs.jeyz9.condoswiftapi.repository.OrderRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class StripeWebhookService {

    private final OrderRepository orderRepository;
    private final CreditTransactionRepository creditTransactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public StripeWebhookService(OrderRepository orderRepository, CreditTransactionRepository creditTransactionRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.creditTransactionRepository = creditTransactionRepository;
        this.userRepository = userRepository;
    }

//    @Transactional
//    public void handleCheckoutCompleted(Session session) {
//        Long orderId = Long.valueOf(session.getMetadata().get("orderId"));
//        
//        Order order = orderRepository.findById(orderId).orElseThrow();
//        
//        if(order.getStatus() == OrderStatus.PAID){
//            return;
//        }
//
//        User user = order.getUser();
//        int credit = order.getPackages().getCreditAmount();
//        
//        order.setStatus(OrderStatus.PAID);
//        order.setPaidAt(LocalDateTime.now());
//        
//        user.setCreditBalance(user.getCreditBalance().add(BigDecimal.valueOf(credit)));
//        userRepository.save(user);
//        
//        creditTransactionRepository.save(
//                CreditTransaction.builder()
//                        .user(user)
//                        .order(order)
//                        .creditAmount(credit)
//                        .type("IN")
//                        .createdAt(LocalDateTime.now())
//                        .build()
//        );
//    }

    @Transactional
    public void handleCheckoutCompleted(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        if (order.getStatus() == OrderStatus.PAID) {
            return;
        }

        User user = order.getUser();
        int credit = order.getPackages().getCreditAmount();

        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());

        user.setCreditBalance(user.getCreditBalance().add(BigDecimal.valueOf(credit)));
        userRepository.save(user);

        creditTransactionRepository.save(
                CreditTransaction.builder()
                        .user(user)
                        .order(order)
                        .creditAmount(credit)
                        .type("IN")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }
}
