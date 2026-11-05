package com.AgricultureFMSystem.AgricultureFMSystem.entity;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.FieldStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA - Field Entity
 * ============================================================================================
 * Represents an individual agricultural plot/parcel inside a Farm.
 * 
 * Relationships:
 * - Field -> Farm: Many-to-One (@ManyToOne)
 * - Field -> Crop: Many-to-One (@ManyToOne, current active crop)
 * - Field -> Activities: One-to-Many (@OneToMany)
 * - Field -> Harvests: One-to-Many (@OneToMany)
 * - Field -> Expenses: One-to-Many (@OneToMany)
 */
@Entity
@Table(name = "fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Field extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name; // e.g., "North Plot A", "Riverside Parcel 2"

    @Column(name = "area_in_acres", nullable = false)
    private Double areaInAcres;

    @Column(name = "soil_type", length = 60)
    private String soilType; // e.g., "Alluvial", "Black Soil", "Red Sandy Loam", "Clay"

    @Column(name = "irrigation_type", length = 60)
    private String irrigationType; // e.g., "Drip Irrigation", "Center Pivot", "Rainfed", "Canal"

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private FieldStatus status = FieldStatus.AVAILABLE;

    // Many Fields belong to One Farm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    // Optional active crop currently sown in this field
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_crop_id")
    private Crop currentCrop;

    // Field-level Activities (Planting, Irrigation, Spraying, etc.)
    @Builder.Default
    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Activity> activities = new ArrayList<>();

    // Harvest records collected from this field
    @Builder.Default
    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Harvest> harvests = new ArrayList<>();

    // Expenses incurred for this specific field
    @Builder.Default
    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Expense> expenses = new ArrayList<>();

    // Crop plans assigned to this field
    @Builder.Default
    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CropPlan> cropPlans = new ArrayList<>();
}
