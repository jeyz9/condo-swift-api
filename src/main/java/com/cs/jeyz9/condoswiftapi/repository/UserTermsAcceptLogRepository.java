package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.models.UserTermsAcceptLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTermsAcceptLogRepository extends JpaRepository<UserTermsAcceptLog, Long> {
}
