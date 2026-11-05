package com.AgricultureFMSystem.AgricultureFMSystem.repository;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.CropPlan;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropPlanStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropSeason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA Repository - CropPlanRepository (Crop Planning)
 * ============================================================================================
 */
@Repository
public interface CropPlanRepository extends JpaRepository<CropPlan, Long> {

    Page<CropPlan> findByFieldId(Long fieldId, Pageable pageable);

    Page<CropPlan> findByStatus(CropPlanStatus status, Pageable pageable);

    Page<CropPlan> findBySeason(CropSeason season, Pageable pageable);

    List<CropPlan> findByFieldIdAndStatus(Long fieldId, CropPlanStatus status);

    // Date filtering: Plans starting between date range
    @Query("SELECT cp FROM CropPlan cp WHERE " +
           "cp.plannedStartDate BETWEEN :startDate AND :endDate")
    Page<CropPlan> findPlansByDateRange(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        Pageable pageable);
}
