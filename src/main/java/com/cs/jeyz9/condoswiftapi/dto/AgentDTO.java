package com.cs.jeyz9.condoswiftapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgentDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private String phone;
    private String lineId;
    
    @JsonProperty("is_verify")
    private Boolean isVerify;
}
