package com.cs.jeyz9.condoswiftapi.dto;

import java.time.LocalDateTime;

public interface AnnounceApproveDTO {
    Long getId();
    String getTitle();
    String getAgenName();
    String getType();
    Double getPrice();
    String getStatus();
    LocalDateTime getAnnouncementDate();
    LocalDateTime getApproveDate();
}
