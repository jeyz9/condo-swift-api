package com.cs.jeyz9.condoswiftapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationDTO {
//    private Long sender;
    private String title;
    private String message;
    private List<Long> userIds = new ArrayList<>();
    private String sendType;
}
