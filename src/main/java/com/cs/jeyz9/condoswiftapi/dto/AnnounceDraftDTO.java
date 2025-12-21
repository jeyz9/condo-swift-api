package com.cs.jeyz9.condoswiftapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnounceDraftDTO {
    private Long id;
    private String title;
    private Double price;
    private AnnounceImageDTO imageList;
    private String address;
}
