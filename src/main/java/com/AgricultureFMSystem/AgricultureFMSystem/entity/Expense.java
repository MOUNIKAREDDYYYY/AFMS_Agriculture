package com.AgricultureFMSystem.AgricultureFMSystem.entity;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA - Expense Entity (Expense Reports Concept)
 * ============================================================================================
 * Represents financial outflows associated with a Farm, and optionally tied to a Field,
 * Crop, or specific Activity.
 */
@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 150)
    private String title; // E.g., "Purchased 10 Bags of Urea", "Tractor diesel 50L"

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 40)
    private ExpenseCategory category;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // CASH, BANK_TRANSFER, UPI, CHEQUE

    @Column(name = "vendor_name", length = 120)
    private String vendorName;

    @Column(name = "notes", length = 500)
    private String notes;

    // Farm where expense was incurred
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    // Optional: specific field the expense was utilized in
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    private Field field;

    // Optional: crop associated with this expenditure
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id")
    private Crop crop;

    // Optional: direct activity that incurred this expense
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;
}
