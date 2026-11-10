package com.AgricultureFMSystem.AgricultureFMSystem.dto.request;

import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropCategory;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropSeason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating and updating Crops.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropRequestDTO {

    @NotBlank(message = "Crop name is required")
    @Size(min = 2, max = 100, message = "Crop name must be between 2 and 100 characters")
    private String name;

    @Size(max = 100, message = "Variety cannot exceed 100 characters")
    private String variety;

    @NotNull(message = "Crop category is required")
    private CropCategory category;

    @NotNull(message = "Growing season is required")
    private CropSeason season;

    @NotNull(message = "Growth duration in days is required")
    @Positive(message = "Growth duration must be a positive number of days")
    private Integer growthDurationDays;

    @Size(max = 50, message = "Optimal temperature cannot exceed 50 characters")
    private String optimalTemperature;

    @Size(max = 50, message = "Water requirement cannot exceed 50 characters")
    private String waterRequirement;

    @Positive(message = "Standard yield per acre must be positive")
    private Double standardYieldPerAcre;

    @Size(max = 20, message = "Yield unit cannot exceed 20 characters")
    private String yieldUnit;
}
