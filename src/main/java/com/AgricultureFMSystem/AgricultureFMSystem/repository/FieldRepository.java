package com.AgricultureFMSystem.AgricultureFMSystem.repository;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.Field;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.FieldStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA Repository - FieldRepository
 * ============================================================================================
 */
@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {

    @Query("SELECT f FROM Field f WHERE " +
           "(:farmId IS NULL OR f.farm.id = :farmId) AND " +
           "(:status IS NULL OR f.status = :status)")
    Page<Field> findByFilters(@Param("farmId") Long farmId,
                              @Param("status") FieldStatus status,
                              Pageable pageable);

    @Query("SELECT f FROM Field f WHERE " +
           "LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.soilType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.irrigationType) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Field> searchFields(@Param("query") String query, Pageable pageable);
}
