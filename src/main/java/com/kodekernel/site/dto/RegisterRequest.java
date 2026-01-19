package com.kodekernel.site.dto;

import com.kodekernel.site.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private com.kodekernel.site.entity.Country country;
    private String password;
    private String role; // Optional, defaults to USER if null
}
