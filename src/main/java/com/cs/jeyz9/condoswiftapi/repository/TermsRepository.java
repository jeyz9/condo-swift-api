package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.models.Terms;
import com.cs.jeyz9.condoswiftapi.models.TermsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {
    Optional<Terms> findByTypeAndIsActiveTrue(TermsType type);
}
