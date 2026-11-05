package com.AgricultureFMSystem.AgricultureFMSystem.dto.request;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.ExpenseCategory;
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
 * Request payload for logging agricultural expenditures.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequestDTO {

    @NotBlank(message = "Expense title is required")
    @Size(min = 2, max = 150, message = "Expense title must be between 2 and 150 characters")
    private String title;

    @NotNull(message = "Expense category is required")
    private ExpenseCategory category;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    @Size(max = 50, message = "Payment method cannot exceed 50 characters")
    private String paymentMethod;

    @Size(max = 120, message = "Vendor name cannot exceed 120 characters")
    private String vendorName;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @NotNull(message = "Farm ID is required")
    private Long farmId;

    private Long fieldId;    // Optional field attachment

    private Long cropId;     // Optional crop attachment

    private Long activityId; // Optional activity attachment
}
