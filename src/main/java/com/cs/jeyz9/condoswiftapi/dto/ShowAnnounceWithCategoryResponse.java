package com.cs.jeyz9.condoswiftapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShowAnnounceWithCategoryResponse {
    private List<RecommendAnnounceDTO> recommendAnnounces = new ArrayList<>();
    private List<AnnounceNearDTO> nearbyPlaces = new ArrayList<>();
    private List<AnnounceByTypeDTO> luxuryHouses = new ArrayList<>();
    private List<AnnounceNearDTO> villaProvince = new ArrayList<>();
}
