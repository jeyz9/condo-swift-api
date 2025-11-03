package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.models.Announce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnounceRepository extends JpaRepository<Announce, Long> {
    @Query("SELECT a FROM Announce a WHERE a.user.id = :userId")
    List<Announce> findAllByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Announce a ORDER BY a.announcementDate DESC")
    List<Announce> findAll();
}
