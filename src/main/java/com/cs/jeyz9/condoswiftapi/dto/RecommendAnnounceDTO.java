package com.cs.jeyz9.condoswiftapi.dto;

import com.cs.jeyz9.condoswiftapi.models.Badge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendAnnounceDTO {
    private Long id;
    private String title;
    private Double price;
    private Integer bathroomCount;
    private Integer bedroomCount;
    private Integer areaSize;
    private String image;
    private Set<Badge> badges;
}
