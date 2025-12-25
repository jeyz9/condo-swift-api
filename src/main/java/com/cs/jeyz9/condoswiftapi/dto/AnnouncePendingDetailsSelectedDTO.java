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
public class AnnouncePendingDetailsSelectedDTO {
    private Long id;
    private String title;
    private String location;
    private Double price;
    private List<AnnounceImageDTO> imageList;
    private Integer bathroomCount;
    private Integer bedroomCount;
    private Integer areaSize;
    private Boolean hasPool;
    private Boolean hasConvenienceStore;
    private Boolean hasFitness;
    private Boolean hasElevator;
    private Boolean hasParking;
    private Boolean hasSecurity;
    private MapPointDTO mapPoint;
    private AgentDTO agent;
    private List<Badge> badges;
    private LocalDateTime announcementDate;
    
    private List<AnnounceDuplicateDTO> exactDuplicates;

    private List<AnnounceDuplicateDTO> similarDuplicates;
}
