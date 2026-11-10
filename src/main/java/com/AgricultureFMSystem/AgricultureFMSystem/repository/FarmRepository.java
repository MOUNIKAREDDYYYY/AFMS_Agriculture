package com.AgricultureFMSystem.AgricultureFMSystem.repository;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA Repository - FarmRepository
 * ============================================================================================
 */
@Repository
public interface FarmRepository extends JpaRepository<Farm, Long> {

    // Derived Query Method: Retrieve farms by farmer ID with pagination
    Page<Farm> findByFarmerId(Long farmerId, Pageable pageable);

    List<Farm> findByFarmerId(Long farmerId);

    @Query("SELECT f FROM Farm f LEFT JOIN FETCH f.farmer")
    List<Farm> findAllWithFarmer();

    // Search farms by name or location
    @Query("SELECT f FROM Farm f WHERE " +
           "LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.location) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Farm> searchFarms(@Param("query") String query, Pageable pageable);
}
