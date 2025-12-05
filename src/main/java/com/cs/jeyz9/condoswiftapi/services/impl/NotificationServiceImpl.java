package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.dto.NotificationDTO;
import com.cs.jeyz9.condoswiftapi.dto.SendNotificationDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Notification;
import com.cs.jeyz9.condoswiftapi.models.NotificationRecipient;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.repository.NotificationRecipientRepository;
import com.cs.jeyz9.condoswiftapi.repository.NotificationRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository, NotificationRecipientRepository notificationRecipientRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
    }

    @Override
    @Transactional
    public String sendNotification(String email, SendNotificationDTO request) {
        try {
            LocalDateTime expired = LocalDateTime.now().plusDays(7);
            User sender = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
            Notification notification = Notification.builder()
                    .id(null)
                    .title(request.getTitle())
                    .message(request.getMessage())
                    .sender(sender)
                    .createdDate(LocalDateTime.now())
                    .expired(expired)
                    .build();
            
            notificationRepository.save(notification);
            
            if (request.getSendType().equalsIgnoreCase("ALL")) {
                List<User> allUser = userRepository.findAll();
                List<NotificationRecipient> sendNotification = allUser.stream().map(u ->
                        NotificationRecipient.builder()
                                .id(null)
                                .recipient(u)
                                .notification(notification)
                                .expiredDate(expired)
                                .isRead(false)
                                .build()).toList();
                notificationRecipientRepository.saveAll(sendNotification);
            } else if (request.getSendType().equalsIgnoreCase("SELECTED")) {
                List<User> selectedUser = userRepository.findAllById(request.getUserIds());
                List<NotificationRecipient> sendNotification = selectedUser.stream().map(u -> 
                    NotificationRecipient.builder()
                            .recipient(u)
                            .notification(notification)
                            .expiredDate(expired)
                            .isRead(false)
                            .build()
                ).toList();
                notificationRecipientRepository.saveAll(sendNotification);
            }

            return "Send notification success.";
        }catch (WebException e) {
            throw e;
        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public List<NotificationDTO> showAllNotificationSelectedByUserId(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
            List<NotificationRecipient> notifyRecipientList = notificationRecipientRepository.findAllByUserId(user.getId());
            return notifyRecipientList.stream().map(n -> {
                Notification notify = notificationRepository.findById(n.getNotification().getId())
                        .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Notification not found."));
                return NotificationDTO.builder()
                        .id(notify.getId())
                        .title(notify.getTitle())
                        .message(notify.getMessage())
                        .isRead(n.getIsRead())
                        .createdDate(notify.getCreatedDate())
                        .build();
            }).toList();
        }catch (WebException e){
            throw e;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional
    public NotificationDTO showNotificationDetailsSelected(Long userId, Long notifyId) {
        try {
            NotificationRecipient notificationRecipient = notificationRecipientRepository.findByUserIdAndNotificationId(userId, notifyId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Notification Recipient not found."));
            Notification notify = notificationRepository.findById(notificationRecipient.getNotification().getId()).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Notification not found."));
            notificationRecipient.setIsRead(true);
            notificationRecipientRepository.save(notificationRecipient);
            return NotificationDTO.builder()
                    .id(notify.getId())
                    .title(notify.getTitle())
                    .message(notify.getMessage())
                    .isRead(notificationRecipient.getIsRead())
                    .createdDate(notify.getCreatedDate())
                    .build();
        }catch (WebException e){
            throw e;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional
    public String deleteNotification(Long notifyId) {
        try {
            Notification notify = notificationRepository.findById(notifyId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Notification not found."));
            notificationRecipientRepository.deleteByNotification(notify);
            notificationRepository.delete(notify);
            return "Delete notification success.";
        }catch (WebException e){
            throw e;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void systemSendNotification(User user, String title, String message) {
        LocalDateTime expired = LocalDateTime.now().plusDays(7);
        Notification notification = Notification.builder()
                .id(null)
                .sender(null)
                .title(title)
                .message(message)
                .createdDate(LocalDateTime.now())
                .expired(expired)
                .build();
        notificationRepository.save(notification);
        NotificationRecipient notify = NotificationRecipient.builder()
                .recipient(user)
                .notification(notification)
                .expiredDate(expired)
                .isRead(false)
                .build();
        notificationRecipientRepository.save(notify);
    }
}
