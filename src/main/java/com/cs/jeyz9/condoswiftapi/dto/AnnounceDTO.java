package com.cs.jeyz9.condoswiftapi.dto;

import com.cs.jeyz9.condoswiftapi.models.NearbyPlace;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnounceDTO {
    private Long id;
    private String title;
    private String location;
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
    private Long userId;
    private Long approveStatusId;
    private Set<Long> badges;
    private List<MapPointDTO> mapPoints;
    private Long announceType;
    private Long saleType;
    private Set<Long> nearbyPlaces;
    
//    @JsonIgnore
//    private List<MultipartFile> imageList;
}
