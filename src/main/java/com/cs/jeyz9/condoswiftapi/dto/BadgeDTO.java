package com.cs.jeyz9.condoswiftapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BadgeDTO {
    private Long id;
    private String badgeName;
    private Integer totalAnnounce;
}
