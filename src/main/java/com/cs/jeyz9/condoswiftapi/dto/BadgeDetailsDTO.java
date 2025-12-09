package com.cs.jeyz9.condoswiftapi.dto;

import java.time.LocalDateTime;

public interface BadgeDetailsDTO {
    Long getId();
    String getBadgeName();
    LocalDateTime getExpiredAt();
}
