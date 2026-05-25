package com.cs.jeyz9.condoswiftapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AgentProfileDTO extends UserProfileOverviewDTO {
    private Integer announceSellCount;
    private Integer announceRentCount;
    private List<AnnounceByTypeDTO> announceList;
}
