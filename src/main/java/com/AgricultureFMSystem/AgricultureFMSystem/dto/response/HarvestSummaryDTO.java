package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ============================================================================================
 * Phase 3: Aggregation DTO - Harvest Yield Summary by Crop
 * ============================================================================================
 * Used by JPA Repository aggregation queries to compute total yield and revenue per crop.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HarvestSummaryDTO {

    private Long cropId;
    private String cropName;
    private Double totalQuantity;
    private String unit;
    private BigDecimal totalRevenue;
    private Long harvestCount;
}
