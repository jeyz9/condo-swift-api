package com.cs.jeyz9.condoswiftapi.dto;

import com.cs.jeyz9.condoswiftapi.models.AnnounceType;
import com.cs.jeyz9.condoswiftapi.models.Badge;
import com.cs.jeyz9.condoswiftapi.models.Province;
import com.cs.jeyz9.condoswiftapi.models.SaleType;
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
    private Integer bathroomCount;
    private Integer bedroomCount;
    private Double areaSize;
    private Boolean hasPool;
    private Boolean hasConvenienceStore;
    private Boolean hasFitness;
    private Boolean hasElevator;
    private Boolean hasParking;
    private Boolean hasSecurity;
    private MapPointDTO mapPoint;
    private AgentDTO owner;
    private List<AgentDTO> agents;
    private List<String> badges;
    private LocalDateTime announcementDate;
    
    private SaleType saleType;
    private AnnounceType announceType;
    private Province province;
    private AnnounceAgentDTO announceAgent;
    private Long approveStatusId;
}
