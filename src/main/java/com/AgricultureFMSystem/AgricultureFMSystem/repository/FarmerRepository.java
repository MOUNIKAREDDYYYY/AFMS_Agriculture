package com.AgricultureFMSystem.AgricultureFMSystem.repository;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farmer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA Repository - FarmerRepository
 * ============================================================================================
 * Provides query methods and custom JPQL searches for Farmers.
 */
@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    // Derived Query Method to verify uniqueness
    boolean existsByEmail(String email);

    Optional<Farmer> findByEmail(String email);

    // Filter by active status with pagination
    Page<Farmer> findByActive(Boolean active, Pageable pageable);

    // Business Search API: Case-insensitive search on name, email, or government ID
    @Query("SELECT f FROM Farmer f WHERE " +
           "LOWER(f.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.governmentIdNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Farmer> searchFarmers(@Param("query") String query, Pageable pageable);
}
