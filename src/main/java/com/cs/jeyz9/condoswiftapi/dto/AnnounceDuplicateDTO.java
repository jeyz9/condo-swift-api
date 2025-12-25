package com.cs.jeyz9.condoswiftapi.dto;

import lombok.Data;

@Data
public class AnnounceDuplicateDTO {

    private Long id;
    private String title;
    private String status;
    private Double similarity;

}
