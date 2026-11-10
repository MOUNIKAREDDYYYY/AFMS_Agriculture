package com.AgricultureFMSystem.AgricultureFMSystem.entity;

import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropPlanStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropSeason;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA - CropPlan Entity (Crop Planning Concept)
 * ============================================================================================
 * Enables agronomists and farmers to plan crop cycles in advance for specific fields.
 * Supports status transitions: DRAFT -> APPROVED -> IN_PROGRESS -> COMPLETED / CANCELLED.
 */
@Entity
@Table(name = "crop_plans", indexes = {
        @Index(name = "idx_crop_plans_start_date", columnList = "planned_start_date"),
        @Index(name = "idx_crop_plans_status", columnList = "status"),
        @Index(name = "idx_crop_plans_season", columnList = "season")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropPlan {

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

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
