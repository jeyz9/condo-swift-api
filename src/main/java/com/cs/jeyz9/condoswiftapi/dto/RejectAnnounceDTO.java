package com.cs.jeyz9.condoswiftapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class RejectAnnounceDTO {
    @NotBlank
    private String remark;
}
