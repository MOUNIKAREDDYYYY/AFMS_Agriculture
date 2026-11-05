package com.AgricultureFMSystem.AgricultureFMSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
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
 * Request payload for creating and updating harvest yield records.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HarvestRequestDTO {

    @NotNull(message = "Harvest date is required")
    private LocalDate harvestDate;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Double quantity;

    @NotBlank(message = "Yield unit is required (e.g., KG, QUINTAL, TON)")
    @Size(max = 20, message = "Unit cannot exceed 20 characters")
    private String unit;

    @Size(max = 50, message = "Quality grade cannot exceed 50 characters")
    private String qualityGrade;

    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;

    private BigDecimal totalRevenue; // Optional, auto-calculated if unitPrice is present

    @Size(max = 100, message = "Storage location cannot exceed 100 characters")
    private String storageLocation;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @NotNull(message = "Field ID is required")
    private Long fieldId;

    @NotNull(message = "Crop ID is required")
    private Long cropId;
}
