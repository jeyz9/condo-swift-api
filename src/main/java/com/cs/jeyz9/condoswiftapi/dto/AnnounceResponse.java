package com.cs.jeyz9.condoswiftapi.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnounceResponse {
    private List<ShowAllAnnounceDetailsWithAgent> announceDetailsWithAgents;
    
    @Min(0)
    private Integer page;
    
    @Min(1)
    private Integer size;
    
    @Min(0)
    private Integer total;
}
