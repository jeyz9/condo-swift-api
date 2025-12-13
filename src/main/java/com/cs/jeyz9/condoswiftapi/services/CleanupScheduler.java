package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.repository.AnnounceBadgeRepository;
import com.cs.jeyz9.condoswiftapi.repository.NotificationRepository;
import com.cs.jeyz9.condoswiftapi.repository.PasswordResetTokenRepository;
import com.cs.jeyz9.condoswiftapi.repository.VerificationOtpTokenRepository;
import com.cs.jeyz9.condoswiftapi.repository.VerificationTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CleanupScheduler {
    private final AnnounceBadgeRepository announceBadgeRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final VerificationOtpTokenRepository verificationOtpTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    public CleanupScheduler(AnnounceBadgeRepository announceBadgeRepository, NotificationRepository notificationRepository, PasswordResetTokenRepository passwordResetTokenRepository, VerificationOtpTokenRepository verificationOtpTokenRepository, VerificationTokenRepository verificationTokenRepository) {
        this.announceBadgeRepository = announceBadgeRepository;
        this.notificationRepository = notificationRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.verificationOtpTokenRepository = verificationOtpTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void removeExpired() {
        LocalDateTime now = LocalDateTime.now();
        announceBadgeRepository.deleteAllExpired(now);
        notificationRepository.deleteAllExpired(now);
        passwordResetTokenRepository.deleteAllExpired(now);
        verificationOtpTokenRepository.deleteAllExpired(now);
        verificationTokenRepository.deleteAllExpired(now);
    }
}
