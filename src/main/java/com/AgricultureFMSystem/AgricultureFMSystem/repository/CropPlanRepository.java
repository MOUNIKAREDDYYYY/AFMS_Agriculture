package com.AgricultureFMSystem.AgricultureFMSystem.repository;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.CropPlan;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropPlanStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropSeason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA Repository - CropPlanRepository (Crop Planning)
 * ============================================================================================
 */
@Repository
public interface CropPlanRepository extends JpaRepository<CropPlan, Long> {

    @Query("SELECT cp FROM CropPlan cp WHERE " +
           "(:fieldId IS NULL OR cp.field.id = :fieldId) AND " +
           "(:status IS NULL OR cp.status = :status) AND " +
           "(:season IS NULL OR cp.season = :season)")
    Page<CropPlan> findByFilters(@Param("fieldId") Long fieldId,
                                 @Param("status") CropPlanStatus status,
                                 @Param("season") CropSeason season,
                                 Pageable pageable);

    // Search crop plans by field name, crop name, or notes
    @Query("SELECT cp FROM CropPlan cp WHERE " +
           "LOWER(cp.field.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(cp.crop.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(COALESCE(cp.notes, '')) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<CropPlan> searchCropPlans(@Param("query") String query, Pageable pageable);

    // Date filtering: Plans starting between date range
    @Query("SELECT cp FROM CropPlan cp WHERE " +
           "cp.plannedStartDate BETWEEN :startDate AND :endDate")
    Page<CropPlan> findPlansByDateRange(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        Pageable pageable);
}
