package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropPlanStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropSeason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Detailed response for Crop Planning.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropPlanResponseDTO {

    private Long id;
    private Long fieldId;
    private String fieldName;
    private Long cropId;
    private String cropName;
    private CropSeason season;
    private LocalDate plannedStartDate;
    private LocalDate estimatedHarvestDate;
    private Double expectedYieldQuantity;
    private String yieldUnit;
    private BigDecimal estimatedBudget;
    private CropPlanStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
