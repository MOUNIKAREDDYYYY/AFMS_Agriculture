package com.AgricultureFMSystem.AgricultureFMSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA - Farmer Entity
 * ============================================================================================
 * Represents a registered Farmer who owns and manages one or more Farms.
 * 
 * Relationships:
 * - Farmer -> Farms: 1-to-Many (@OneToMany)
 *   A farmer can own multiple farms. Deleting a farmer cascades to delete their farms (CascadeType.ALL).
 *   orphanRemoval = true ensures that removing a farm from the list removes it from the database.
 * 
 * Phase 1: Lombok Annotations
 * - @Getter, @Setter: Generates getter and setter methods.
 * - @NoArgsConstructor: Generates a no-argument constructor required by Hibernate.
 * - @AllArgsConstructor: Generates a constructor with all arguments.
 * - @Builder: Implements the Builder design pattern for readable object instantiation.
 */
@Entity
@Table(name = "farmers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farmer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "government_id_number", length = 50)
    private String governmentIdNumber; // E.g., Farmer ID or Tax ID

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    // 1-to-Many Relationship: One Farmer owns Many Farms
    // mappedBy refers to the "farmer" field in the Farm entity
    @Builder.Default
    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Farm> farms = new ArrayList<>();

    /**
     * Helper synchronization method to maintain bidirectional relationship consistency.
     */
    public void addFarm(Farm farm) {
        farms.add(farm);
        farm.setFarmer(this);
    }

    public void removeFarm(Farm farm) {
        farms.remove(farm);
        farm.setFarmer(null);
    }
}
