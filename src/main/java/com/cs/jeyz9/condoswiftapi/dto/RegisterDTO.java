package com.cs.jeyz9.condoswiftapi.dto;

import com.cs.jeyz9.condoswiftapi.models.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterDTO {
    private Long id;
    
    private String name;

    private String description;

    private String image;

    @Column(unique = true)
    @Pattern(regexp = "\\d{10}", message = "เบอร์โทรต้องมี 10 หลัก")
    private String phone;

    @Email
    @Column(unique = true)
    private String email;

    @Pattern(
            regexp = "^(?=.*[A-Za-z]).{8,}$",
            message = "รหัสผ่านต้องมีอย่างน้อย 8 ตัวอักษร และต้องมีตัวอักษรอย่างน้อย 1 ตัว"
    )
    @NotBlank(message = "จำเป็นต้องใส่รหัสผ่าน")
    private String password;

    @NotBlank(message = "จำเป็นต้องยืนยันรหัสผ่าน")
    private String ConfirmPassword;
    
    private Boolean phoneVerified;

    private Boolean emailVerified;

    @JsonProperty("is_agen")
    private Boolean isAgen;

    public RegisterDTO(String name, String description, String image, String phone, String email, String password, Boolean isAgen){
        this.name = name;
        this.description = description;
        this.image = image;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.phoneVerified = false;
        this.emailVerified = false;
        this.isAgen = isAgen;
    }
}
