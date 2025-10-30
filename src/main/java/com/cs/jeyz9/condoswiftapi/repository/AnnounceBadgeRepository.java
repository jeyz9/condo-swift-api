package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.models.AnnounceBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface AnnounceBadgeRepository extends JpaRepository<AnnounceBadge, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM AnnounceBadge ab WHERE ab.expiresAt < :now")
    void deleteAllExpired(@Param("now")LocalDateTime now);
}
