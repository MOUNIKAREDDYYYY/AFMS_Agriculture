package com.AgricultureFMSystem.AgricultureFMSystem.repository;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.Activity;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityType;
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
 * Phase 3: Spring Data JPA Repository - ActivityRepository (Activity Tracking)
 * ============================================================================================
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT a FROM Activity a WHERE " +
           "(:fieldId IS NULL OR a.field.id = :fieldId) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:activityType IS NULL OR a.activityType = :activityType)")
    Page<Activity> findByFilters(@Param("fieldId") Long fieldId,
                                 @Param("status") ActivityStatus status,
                                 @Param("activityType") ActivityType activityType,
                                 Pageable pageable);

    // Business API: Pending activities (PLANNED or IN_PROGRESS), eagerly fetching field for scheduler & DTO mapping
    @Query("SELECT a FROM Activity a LEFT JOIN FETCH a.field LEFT JOIN FETCH a.crop WHERE a.status IN ('PLANNED', 'IN_PROGRESS') ORDER BY a.scheduledDate ASC")
    List<Activity> findPendingActivities();

    // Date filtering: Activities scheduled between date range
    @Query("SELECT a FROM Activity a WHERE a.scheduledDate BETWEEN :startDate AND :endDate")
    Page<Activity> findByScheduledDateBetween(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate,
                                              Pageable pageable);

    // Search activities by title, notes, or assigned person
    @Query("SELECT a FROM Activity a WHERE " +
           "LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.assignedTo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.notes) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Activity> searchActivities(@Param("query") String query, Pageable pageable);

    long countByFieldFarmIdAndStatus(Long farmId, ActivityStatus status);

    @Query("SELECT a.field.farm.id, COUNT(a) FROM Activity a WHERE a.status = :status GROUP BY a.field.farm.id")
    List<Object[]> countByStatusGroupedByFarm(@Param("status") ActivityStatus status);
}
