package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Detailed response payload representing an Expense record.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponseDTO {

    private Long id;
    private String title;
    private ExpenseCategory category;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String paymentMethod;
    private String vendorName;
    private String notes;
    private Long farmId;
    private String farmName;
    private Long fieldId;
    private String fieldName;
    private Long cropId;
    private String cropName;
    private Long activityId;
    private String activityTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
