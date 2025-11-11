package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.models.AnnounceStateApprove;
import com.cs.jeyz9.condoswiftapi.models.ApproveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnounceStateApproveRepository extends JpaRepository<AnnounceStateApprove, Long> {
    AnnounceStateApprove findByStatusName(ApproveStatus statusName);
}
