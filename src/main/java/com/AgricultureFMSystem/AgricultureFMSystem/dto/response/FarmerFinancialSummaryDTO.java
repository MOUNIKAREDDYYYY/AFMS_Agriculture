package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Comprehensive financial statement aggregated at the Farmer level across all their owned farms.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmerFinancialSummaryDTO {

    private Long farmerId;
    private String farmerName;
    private String email;
    private String phoneNumber;
    private int totalFarms;
    private BigDecimal totalExpenses;
    private BigDecimal totalHarvestRevenue;
    private BigDecimal netProfitOrLoss;
    private long totalActivitiesCompleted;
    private long totalHarvestEvents;
}
