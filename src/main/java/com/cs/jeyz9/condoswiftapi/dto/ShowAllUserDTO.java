package com.cs.jeyz9.condoswiftapi.dto;

import com.cs.jeyz9.condoswiftapi.models.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ShowAllUserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String lineId;
    private Set<Role> roles;
    private BigDecimal creditBalance;
}
