package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Detailed representation of a Farm returned by the API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmResponseDTO {

    private Long id;
    private String name;
    private String location;
    private Double totalAreaInAcres;
    private Long farmerId;
    private String farmerName;
    private int totalFields;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
