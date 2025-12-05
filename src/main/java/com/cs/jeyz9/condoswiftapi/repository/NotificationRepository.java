package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification nf WHERE nf.expired < :now")
    void deleteAllExpired(@Param("now") LocalDateTime now);
}
