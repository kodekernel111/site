package com.kodekernel.site.dto;

import com.kodekernel.site.entity.Country;
import com.kodekernel.site.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Country country;
    private java.util.Set<String> roles;
    private String displayRole;
    private String bio;
    private boolean showOnTeam;
}
