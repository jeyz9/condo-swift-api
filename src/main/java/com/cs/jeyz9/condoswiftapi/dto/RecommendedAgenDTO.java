package com.cs.jeyz9.condoswiftapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedAgenDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private String phone;
    private String lineId;
    private Boolean isVerified;
}
