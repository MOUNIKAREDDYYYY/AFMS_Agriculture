package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import com.AgricultureFMSystem.AgricultureFMSystem.enums.FieldStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response payload representing a Field.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldResponseDTO {

    private Long id;
    private String name;
    private Double areaInAcres;
    private String soilType;
    private String irrigationType;
    private FieldStatus status;
    private Long farmId;
    private String farmName;
    private Long currentCropId;
    private String currentCropName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
