package com.cs.jeyz9.condoswiftapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StationsDTO {
    private Long id;
    private String name;
    private String stationType;
}
