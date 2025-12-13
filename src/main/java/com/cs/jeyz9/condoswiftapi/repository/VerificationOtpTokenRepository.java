package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.models.VerificationOtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationOtpTokenRepository extends JpaRepository<VerificationOtpToken, Long> {
    Optional<VerificationOtpToken> findByToken(String token);
    
    Optional<VerificationOtpToken> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM VerificationOtpToken WHERE expiredAt < :now
    """)
    void deleteAllExpired(@Param("now") LocalDateTime now);
}
