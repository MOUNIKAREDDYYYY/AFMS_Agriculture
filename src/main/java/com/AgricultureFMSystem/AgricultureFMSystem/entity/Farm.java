package com.AgricultureFMSystem.AgricultureFMSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA - Farm Entity
 * ============================================================================================
 * Represents a specific agricultural property owned by a Farmer.
 * 
 * Relationships:
 * - Farm -> Farmer: Many-to-One (@ManyToOne) with FetchType.LAZY to prevent N+1
 * query problems.
 * - Farm -> Fields: One-to-Many (@OneToMany) with CascadeType.ALL.
 * - Farm -> Expenses: One-to-Many (@OneToMany) for tracking farm-level
 * expenditures.
 */
@Entity
@Table(name = "farms")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "location", nullable = false, length = 200)
    private String location; // Village / District / GPS Coordinate description

    @Column(name = "total_area_in_acres", nullable = false)
    private Double totalAreaInAcres;

    // Many Farms belong to One Farmer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    // One Farm contains Multiple Fields/Plots
    @Builder.Default
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Field> fields = new ArrayList<>();

    // Expenses incurred for this farm
    @Builder.Default
    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Expense> expenses = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addField(Field field) {
        fields.add(field);
        field.setFarm(this);
    }

    public void removeField(Field field) {
        fields.remove(field);
        field.setFarm(null);
    }
}
