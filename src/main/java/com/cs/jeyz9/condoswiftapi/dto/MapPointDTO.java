package com.cs.jeyz9.condoswiftapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MapPointDTO {
    private Long id;
    private String lat;
    private String lng;
    
    public MapPointDTO(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
