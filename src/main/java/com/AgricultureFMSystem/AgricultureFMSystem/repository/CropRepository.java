package com.AgricultureFMSystem.AgricultureFMSystem.repository;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.Crop;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropCategory;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropSeason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA Repository - CropRepository
 * ============================================================================================
 */
@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

    Page<Crop> findByCategory(CropCategory category, Pageable pageable);

    Page<Crop> findBySeason(CropSeason season, Pageable pageable);

    @Query("SELECT c FROM Crop c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.variety) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Crop> searchCrops(@Param("query") String query, Pageable pageable);
}
