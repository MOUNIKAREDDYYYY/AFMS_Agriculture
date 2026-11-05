package com.AgricultureFMSystem.AgricultureFMSystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA & Auditing Base Entity
 * ============================================================================================
 * - @MappedSuperclass: Inherits persistent properties (createdAt, updatedAt) into child entities
 *   without creating a separate database table for BaseEntity.
 * - @EntityListeners(AuditingEntityListener.class): Automatically captures creation and 
 *   modification timestamps during entity persistence lifecycle.
 * - Lombok @Getter, @Setter: Generates boilerplate getters and setters at compile-time (Phase 1).
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    // Timestamp when the record was initially inserted into the database
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Timestamp updated automatically whenever the entity is modified
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
