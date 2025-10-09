package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.models.AnnounceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnounceImageRepository extends JpaRepository<AnnounceImage, Long> {
    List<AnnounceImage> findByAnnounceId(Long announceId);
}
