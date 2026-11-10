package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropCategory;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropSeason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response payload representing a Crop entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropResponseDTO {

    private Long id;
    private String name;
    private String variety;
    private CropCategory category;
    private CropSeason season;
    private Integer growthDurationDays;
    private String optimalTemperature;
    private String waterRequirement;
    private Double standardYieldPerAcre;
    private String yieldUnit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
