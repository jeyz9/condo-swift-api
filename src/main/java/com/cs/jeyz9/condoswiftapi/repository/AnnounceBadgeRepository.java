package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.dto.BadgeDetailsDTO;
import com.cs.jeyz9.condoswiftapi.models.AnnounceBadge;
import com.cs.jeyz9.condoswiftapi.models.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnnounceBadgeRepository extends JpaRepository<AnnounceBadge, Long> {
    
    List<AnnounceBadge> findAllByBadge(Badge badge);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM AnnounceBadge ab WHERE ab.expiresAt < :now")
    void deleteAllExpired(@Param("now")LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM announce_badge ab WHERE ab.announce_id = :announceId AND ab.badge_id = :badgeId", nativeQuery = true)
    void deleteAnnounceBadgesByAnnounceIdAndBadgeId(@Param("announceId") Long announceId, @Param("badgeId") Long badgeId);

    @Query(value = """
        SELECT b.id, b.badge_name, ab.expires_at AS expired_at
        FROM announce_badge ab
        LEFT JOIN badges b ON b.id = ab.badge_id
        WHERE ab.id = :id;
    """, nativeQuery = true)
    Optional<BadgeDetailsDTO> findAnnounceBadgesByAnnounceIdAndBadgeId(@Param("id") Long id);
}
