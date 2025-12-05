package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.NotificationDTO;
import com.cs.jeyz9.condoswiftapi.dto.SendNotificationDTO;
import com.cs.jeyz9.condoswiftapi.models.User;

import java.util.List;

public interface NotificationService {
    String sendNotification(String email, SendNotificationDTO notification);
    List<NotificationDTO> showAllNotificationSelectedByUserId(Long userId);
    NotificationDTO showNotificationDetailsSelected(Long userId, Long notifyId);
    String deleteNotification(Long notifyId);
    void systemSendNotification(User user, String title, String message);
}
