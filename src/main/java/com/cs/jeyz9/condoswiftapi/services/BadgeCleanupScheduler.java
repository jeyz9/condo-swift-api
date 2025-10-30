package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.repository.AnnounceBadgeRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BadgeCleanupScheduler {
    private final AnnounceBadgeRepository announceBadgeRepository;
    
    public BadgeCleanupScheduler(AnnounceBadgeRepository announceBadgeRepository) {
        this.announceBadgeRepository = announceBadgeRepository;
    }
    
    @Scheduled(cron = "0 0 10 * * *")
    public void removeExpiredBadges() {
        announceBadgeRepository.deleteAllExpired(LocalDateTime.now());
    }
}
