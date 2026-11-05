package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * High-level financial reporting DTO showing farm profitability (Revenue vs Expense).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmFinancialSummaryDTO {

    private Long farmId;
    private String farmName;
    private Long farmerId;
    private String farmerName;
    private BigDecimal totalExpenses;
    private BigDecimal totalHarvestRevenue;
    private BigDecimal netProfitOrLoss;
    private long totalActivitiesCompleted;
    private long totalHarvestEvents;
}
