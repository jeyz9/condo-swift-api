package com.cs.jeyz9.condoswiftapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthResponse {
    private Long userId;
    private String accessToken;
    private String tokenType = "Bearer";
}
