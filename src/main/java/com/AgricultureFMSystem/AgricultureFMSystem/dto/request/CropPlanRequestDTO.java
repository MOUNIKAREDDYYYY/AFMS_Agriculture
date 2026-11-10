package com.AgricultureFMSystem.AgricultureFMSystem.dto.request;

import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropPlanStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropSeason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload for creating and updating a Crop Plan.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropPlanRequestDTO {

    @NotNull(message = "Field ID is required")
    private Long fieldId;

    @NotNull(message = "Crop ID is required")
    private Long cropId;

    @NotNull(message = "Season is required")
    private CropSeason season;

    @NotNull(message = "Planned start date is required")
    private LocalDate plannedStartDate;

    @NotNull(message = "Estimated harvest date is required")
    private LocalDate estimatedHarvestDate;

    @Positive(message = "Expected yield quantity must be greater than 0")
    private Double expectedYieldQuantity;

    @Size(max = 20, message = "Yield unit cannot exceed 20 characters")
    private String yieldUnit;

    @Positive(message = "Estimated budget must be positive")
    private BigDecimal estimatedBudget;

    private CropPlanStatus status;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
