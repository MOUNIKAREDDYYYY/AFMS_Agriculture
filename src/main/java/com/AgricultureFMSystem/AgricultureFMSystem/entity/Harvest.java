package com.AgricultureFMSystem.AgricultureFMSystem.entity;

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
 * Phase 3: Spring Data JPA - Harvest Entity (Harvest Records Concept)
 * ============================================================================================
 * Records production yields and harvest outputs from cultivated fields.
 * Includes financial metrics (unit price, total revenue) for yield reporting and profitability calculations.
 */
@Entity
@Table(name = "harvests", indexes = {
        @Index(name = "idx_harvests_harvest_date", columnList = "harvest_date")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Harvest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "harvest_date", nullable = false)
    private LocalDate harvestDate;

    @Column(name = "quantity", nullable = false)
    private Double quantity; // Harvested amount

    @Column(name = "unit", nullable = false, length = 20)
    private String unit; // KG, QUINTAL, TON, CRATES

    @Column(name = "quality_grade", length = 50)
    private String qualityGrade; // Grade A, Grade B, Premium, Export

    @Column(name = "unit_price", precision = 12, scale = 2)
    private BigDecimal unitPrice; // Selling price per unit

    @Column(name = "total_revenue", precision = 12, scale = 2)
    private BigDecimal totalRevenue; // Computed or realized revenue

    @Column(name = "storage_location", length = 100)
    private String storageLocation; // Silo 1, Cold Storage B, Warehouse A

    @Column(name = "notes", length = 500)
    private String notes;

    // Field from which harvest was reaped
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    // Crop that was harvested
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
