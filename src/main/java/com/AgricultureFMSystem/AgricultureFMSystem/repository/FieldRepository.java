package com.AgricultureFMSystem.AgricultureFMSystem.repository;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.Field;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.FieldStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA Repository - FieldRepository
 * ============================================================================================
 */
@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {

    Page<Field> findByFarmId(Long farmId, Pageable pageable);

    List<Field> findByFarmId(Long farmId);

    Page<Field> findByStatus(FieldStatus status, Pageable pageable);

    Page<Field> findByFarmIdAndStatus(Long farmId, FieldStatus status, Pageable pageable);

    @Query("SELECT f FROM Field f WHERE " +
           "LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.soilType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.irrigationType) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Field> searchFields(@Param("query") String query, Pageable pageable);
}
