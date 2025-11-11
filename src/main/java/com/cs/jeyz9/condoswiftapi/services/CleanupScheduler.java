package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.repository.AnnounceBadgeRepository;
import com.cs.jeyz9.condoswiftapi.repository.NotificationRepository;
import com.cs.jeyz9.condoswiftapi.repository.PasswordResetTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CleanupScheduler {
    private final AnnounceBadgeRepository announceBadgeRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public CleanupScheduler(AnnounceBadgeRepository announceBadgeRepository, NotificationRepository notificationRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.announceBadgeRepository = announceBadgeRepository;
        this.notificationRepository = notificationRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }
    
    @Scheduled(cron = "0 0 10 * * *")
    public void removeExpired() {
        announceBadgeRepository.deleteAllExpired(LocalDateTime.now());
        notificationRepository.deleteAllExpired(LocalDateTime.now());
        passwordResetTokenRepository.deleteAllExpired(LocalDateTime.now());
    }
}
