package com.cs.jeyz9.condoswiftapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnounceByAgentRequestDTO {
    private String title;
    private Integer bathroomCount;
    private Integer bedroomCount;
    private Integer areaSize;
    private Boolean hasPool;
    private Boolean hasConvenienceStore;
    private Boolean hasFitness;
    private Boolean hasElevator;
    private Boolean hasParking;
    private Boolean hasSecurity;
}