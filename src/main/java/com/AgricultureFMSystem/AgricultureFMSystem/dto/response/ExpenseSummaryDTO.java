package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import com.AgricultureFMSystem.AgricultureFMSystem.enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ============================================================================================
 * Phase 3: Aggregation DTO - Expense Summary by Category
 * ============================================================================================
 * Used by JPA Repository aggregation queries (SUM, COUNT, AVG) to group expenses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSummaryDTO {

    private ExpenseCategory category;
    private BigDecimal totalAmount;
    private Long expenseCount;
    private Double averageAmount;

    public ExpenseSummaryDTO(ExpenseCategory category, BigDecimal totalAmount, Long expenseCount) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.expenseCount = expenseCount;
        if (totalAmount != null && expenseCount != null && expenseCount > 0) {
            this.averageAmount = totalAmount.doubleValue() / expenseCount;
        }
    }
}
