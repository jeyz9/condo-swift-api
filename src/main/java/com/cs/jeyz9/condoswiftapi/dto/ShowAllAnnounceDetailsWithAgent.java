package com.cs.jeyz9.condoswiftapi.dto;

import com.cs.jeyz9.condoswiftapi.models.Badge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowAllAnnounceDetailsWithAgent {
    private Long id;
    private String title;
    private Double price;
    private AnnounceImageDTO imageList;
    private AgentDTO agent;
    private Set<Badge> badgeSet;
}
