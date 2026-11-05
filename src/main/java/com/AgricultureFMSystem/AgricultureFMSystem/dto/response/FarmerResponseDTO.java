package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ============================================================================================
 * Phase 4: DTO Pattern - Farmer Response Representation
 * ============================================================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmerResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String governmentIdNumber;
    private Boolean active;
    private int totalFarms;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
