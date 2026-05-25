package com.cs.jeyz9.condoswiftapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OwnerProfileDTO extends UserProfileOverviewDTO {

    private Integer announceSellCount;
    private Integer announceRentCount;

    private Integer activeAnnounceCount;
    private Integer closedAnnounceCount;

    private List<AnnounceByTypeDTO> announces;
}