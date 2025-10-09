package com.cs.jeyz9.condoswiftapi.dto;

import com.cs.jeyz9.condoswiftapi.models.Badge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnounceDetailsSelected {
    private Long id;
    private String title;
    private String location;
    private Double price;
    private List<AnnounceImageDTO> imageList;
    private Number bathroomCount;
    private Number bedroomCount;
    private Number areaSize;
    private Boolean hasPool;
    private Boolean hasConvenienceStore;
    private Boolean hasFitness;
    private Boolean hasElevator;
    private Boolean hasParking;
    private Boolean hasSecurity;
    private List<MapPointDTO> mapPointList;
    private AgenDTO agen;
    private List<Badge> badges;
    private LocalDateTime announcementDate;
}
