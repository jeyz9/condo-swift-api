package com.cs.jeyz9.condoswiftapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserProfileOverviewDTO {

    private Long id;
    private String name;
    private String description;
    private String image;
    private String phone;
    private String lineId;

    private LocalDateTime joinAt;

    private Boolean phoneVerified;
    private Boolean emailVerified;

    private Set<String> roles;
}