package com.cs.jeyz9.condoswiftapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {
    @NotEmpty(message = "Password is require!")
    private String oldPassword;
    @Pattern(
            regexp = "^(?=.*[A-Za-z]).{8,}$",
            message = "รหัสผ่านต้องมีอย่างน้อย 8 ตัวอักษร และต้องมีตัวอักษรอย่างน้อย 1 ตัว"
    )
    @NotEmpty(message = "Password is require!")
    private String newPassword;
    @NotEmpty(message = "Password is require!")
    private String confirmPassword;
}
