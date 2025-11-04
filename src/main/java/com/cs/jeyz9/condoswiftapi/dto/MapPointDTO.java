package com.cs.jeyz9.condoswiftapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MapPointDTO {
    private Long id;
    private Double lat;
    private Double lng;
    
    public MapPointDTO(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
