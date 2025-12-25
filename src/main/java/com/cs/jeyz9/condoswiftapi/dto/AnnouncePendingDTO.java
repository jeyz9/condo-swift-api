package com.cs.jeyz9.condoswiftapi.dto;

import java.time.LocalDateTime;

public interface AnnouncePendingDTO {
    Long getId();
    String getTitle();
    String getAgentName();
    String getType();
    Double getPrice();
    String getStatus();
    LocalDateTime getAnnouncementDate();
    LocalDateTime getApproveDate();
    String getImage();
    String getRemark();
    
    Boolean getIsDuplicate();
}
