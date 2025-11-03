package com.cs.jeyz9.condoswiftapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileOverviewDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private LocalDateTime joinAt;
    private Integer announceSellCount;
    private Integer announceRentCount;
    private List<AnnounceByTypeDTO> announceList;
    private Boolean phoneVerified;
    private Boolean emailVerified;
}
