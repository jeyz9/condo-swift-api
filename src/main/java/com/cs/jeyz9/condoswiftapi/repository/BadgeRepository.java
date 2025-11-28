package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.models.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
//    Optional<Badge> findBadgesByBadgeName(String badgeName);
    Optional<Badge> findByBadgeNameIgnoreCase(String badgeName);
    
    @Query(
            value = """
                SELECT b.id, b.badge_name FROM badges b
                JOIN announce_badge ab ON ab.badge_id = b.id
                WHERE ab.announce_id = :announceId;
            """,
            nativeQuery = true
    )
    Set<Badge> findAllBadgeByAnnounceId(@Param("announceId") Long announceId);
    
    @Query(value = """
        SELECT COUNT(b.badge_id) AS totalAnnounce FROM announce_badge b
        WHERE b.badge_id = :badgeId
        GROUP BY b.badge_id
        ORDER BY b.badge_id ASC;
        """,
    nativeQuery = true)
    Optional<Integer> findCountAnnounce(@Param("badgeId") Long badgeId);
}
