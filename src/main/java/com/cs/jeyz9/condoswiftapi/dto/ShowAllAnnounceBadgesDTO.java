package com.cs.jeyz9.condoswiftapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class ShowAllAnnounceBadgesDTO {
    private Long id;
    private String title;
    private String agent;
    private Set<BadgeDetailsDTO> badges;
}
