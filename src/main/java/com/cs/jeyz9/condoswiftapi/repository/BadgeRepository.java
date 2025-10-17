package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.models.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Set<Badge> findBadgesByBadgeName(String badgeName);
}
