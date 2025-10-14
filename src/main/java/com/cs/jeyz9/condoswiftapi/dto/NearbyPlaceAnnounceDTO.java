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
public class NearbyPlaceAnnounceDTO {
    private Long id;
    private String name;
    private Integer totalAnnounces;
}
