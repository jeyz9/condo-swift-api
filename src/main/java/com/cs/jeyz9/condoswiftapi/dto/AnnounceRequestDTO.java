package com.cs.jeyz9.condoswiftapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnounceRequestDTO {
    private String title;
    private String location;
    private String province;
    private Double price;
    private Integer bathroomCount;
    private Integer bedroomCount;
    private Integer areaSize;
    private Boolean hasPool;
    private Boolean hasConvenienceStore;
    private Boolean hasFitness;
    private Boolean hasElevator;
    private Boolean hasParking;
    private Boolean hasSecurity;
    private Long approveStatusId;
    private List<MapPointDTO> mapPoints;
    private Long announceType;
    private Long saleType;
}
