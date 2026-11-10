package com.AgricultureFMSystem.AgricultureFMSystem.entity;

import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropCategory;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropSeason;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA - Crop Entity
 * ============================================================================================
 * Defines botanical and agricultural specifications for crops cultivated across fields.
 */
@Entity
@Table(name = "crops", uniqueConstraints = {
        @UniqueConstraint(name = "uk_crops_name_variety", columnNames = {"name", "variety"})
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name; // e.g., "Wheat", "Rice", "Tomato", "Cotton"

    @Column(name = "variety", length = 100)
    private String variety; // e.g., "Sharbati", "Basmati 1121", "Roma VF"

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private CropCategory category; // GRAIN, VEGETABLE, CASH_CROP, etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "season", nullable = false, length = 30)
    private CropSeason season; // KHARIF, RABI, ZAID, YEAR_ROUND

    @Column(name = "growth_duration_days", nullable = false)
    private Integer growthDurationDays; // Days required from sowing to maturity

    @Column(name = "optimal_temperature", length = 50)
    private String optimalTemperature; // e.g., "20C - 28C"

    @Column(name = "water_requirement", length = 50)
    private String waterRequirement; // e.g., "Medium (450-650 mm)"

    @Column(name = "standard_yield_per_acre")
    private Double standardYieldPerAcre; // Expected baseline yield

    @Column(name = "yield_unit", length = 20)
    private String yieldUnit; // e.g., "KG", "QUINTAL", "TON"

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
