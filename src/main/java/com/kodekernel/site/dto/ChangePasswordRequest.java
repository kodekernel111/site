package com.kodekernel.site.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    private String email; // Or ID, but email is safer if available, or just rely on Token
    private String oldPassword;
    private String newPassword;
}
