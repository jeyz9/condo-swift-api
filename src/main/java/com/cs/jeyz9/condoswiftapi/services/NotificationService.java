package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.models.User;

public interface NotificationService {
    void createNotification(Long userId, String title, String message);
}
