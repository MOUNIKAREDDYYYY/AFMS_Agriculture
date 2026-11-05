package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Detailed representation of a Harvest yield entry.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HarvestResponseDTO {

    private Long id;
    private LocalDate harvestDate;
    private Double quantity;
    private String unit;
    private String qualityGrade;
    private BigDecimal unitPrice;
    private BigDecimal totalRevenue;
    private String storageLocation;
    private String notes;
    private Long fieldId;
    private String fieldName;
    private Long cropId;
    private String cropName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
