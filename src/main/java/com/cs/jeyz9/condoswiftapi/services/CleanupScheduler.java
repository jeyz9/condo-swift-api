package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.repository.AnnounceBadgeRepository;
import com.cs.jeyz9.condoswiftapi.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CleanupScheduler {
    private final AnnounceBadgeRepository announceBadgeRepository;
    private final NotificationRepository notificationRepository;

    public CleanupScheduler(AnnounceBadgeRepository announceBadgeRepository, NotificationRepository notificationRepository) {
        this.announceBadgeRepository = announceBadgeRepository;
        this.notificationRepository = notificationRepository;
    }
    
    @Scheduled(cron = "0 0 10 * * *")
    public void removeExpired() {
        announceBadgeRepository.deleteAllExpired(LocalDateTime.now());
        notificationRepository.deleteAllExpired(LocalDateTime.now());
    }
}
