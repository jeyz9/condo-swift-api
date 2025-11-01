package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.NotificationDTO;
import com.cs.jeyz9.condoswiftapi.dto.SendNotificationDTO;

import java.util.List;

public interface NotificationService {
    String sendNotification(SendNotificationDTO notification);
    List<NotificationDTO> showAllNotificationSelectedByUserId(Long userId);
    NotificationDTO showNotificationDetailsSelected(Long userId, Long notifyId);
    String deleteNotification(Long notifyId);
//    Notification showAllNotificationByAdmin();
}
