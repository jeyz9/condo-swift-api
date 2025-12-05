package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.models.Notification;
import com.cs.jeyz9.condoswiftapi.models.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {
    @Query(value = """
        SELECT nr.id, nr.notify_id, nr.recipient_id, nr.is_read, nr.expired_date FROM notification_recipients nr WHERE nr.recipient_id = :userId;
    """, nativeQuery = true)
    List<NotificationRecipient> findAllByUserId(@Param("userId") Long userId);
    
    @Query(value = """
        SELECT nr.id, nr.notify_id, nr.recipient_id, nr.is_read, nr.expired_date FROM notification_recipients nr WHERE nr.recipient_id = :userId AND nr.notify_id = :notifyId;
    """, nativeQuery = true)
    Optional<NotificationRecipient> findByUserIdAndNotificationId(@Param("userId") Long userId, @Param("notifyId") Long notifyId);

    void deleteByNotification(Notification notification);
}
