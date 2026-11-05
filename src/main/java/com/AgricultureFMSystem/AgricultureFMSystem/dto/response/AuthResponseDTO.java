package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String tokenType;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
    private String message;
}
