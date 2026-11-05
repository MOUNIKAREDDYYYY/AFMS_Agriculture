package com.AgricultureFMSystem.AgricultureFMSystem.entity;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropPlanStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropSeason;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA - CropPlan Entity (Crop Planning Concept)
 * ============================================================================================
 * Enables agronomists and farmers to plan crop cycles in advance for specific fields.
 * Supports status transitions: DRAFT -> APPROVED -> IN_PROGRESS -> COMPLETED / CANCELLED.
 */
@Entity
@Table(name = "crop_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Enumerated(EnumType.STRING)
    @Column(name = "season", nullable = false, length = 30)
    private CropSeason season;

    @Column(name = "planned_start_date", nullable = false)
    private LocalDate plannedStartDate;

    @Column(name = "estimated_harvest_date", nullable = false)
    private LocalDate estimatedHarvestDate;

    @Column(name = "expected_yield_quantity")
    private Double expectedYieldQuantity;

    @Column(name = "yield_unit", length = 20)
    private String yieldUnit;

    @Column(name = "estimated_budget", precision = 12, scale = 2)
    private BigDecimal estimatedBudget;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private CropPlanStatus status = CropPlanStatus.DRAFT;

    @Column(name = "notes", length = 500)
    private String notes;
}
