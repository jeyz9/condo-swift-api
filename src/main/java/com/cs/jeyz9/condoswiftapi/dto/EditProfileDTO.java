package com.cs.jeyz9.condoswiftapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EditProfileDTO {
    private String name;
    private String description;
    
    @Pattern(regexp = "\\d{10}", message = "เบอร์โทรต้องมี 10 หลัก")
    private String phone;

    @Email
    private String email;
    
    private String lineId;
}
