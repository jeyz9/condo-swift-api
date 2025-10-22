package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.models.VerificationOtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationOtpTokenRepository extends JpaRepository<VerificationOtpToken, Long> {
    Optional<VerificationOtpToken> findByToken(String token);
}
