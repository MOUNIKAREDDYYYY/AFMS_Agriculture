package com.AgricultureFMSystem.AgricultureFMSystem.dto.request;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.FieldStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating and managing a Field plot.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldRequestDTO {

    @NotBlank(message = "Field name is required")
    @Size(min = 2, max = 100, message = "Field name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Area in acres is required")
    @Positive(message = "Area in acres must be greater than 0")
    private Double areaInAcres;

    @Size(max = 60, message = "Soil type cannot exceed 60 characters")
    private String soilType;

    @Size(max = 60, message = "Irrigation type cannot exceed 60 characters")
    private String irrigationType;

    private FieldStatus status;

    @NotNull(message = "Farm ID is required")
    private Long farmId;

    private Long currentCropId; // Optional crop currently planted
}
