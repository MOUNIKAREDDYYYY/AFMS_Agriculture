package com.AgricultureFMSystem.AgricultureFMSystem.entity;

import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA - Activity Entity (Activity Tracking Concept)
 * ============================================================================================
 * Tracks agricultural operations performed on fields (e.g. planting, irrigation, fertilizing).
 * Supports scheduling, tracking in-progress work, and recording completion dates and workers.
 */
@Entity
@Table(name = "activities", indexes = {
        @Index(name = "idx_activities_scheduled_date", columnList = "scheduled_date"),
        @Index(name = "idx_activities_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 150)
    private String title; // E.g., "Urea Application 50kg", "Drip irrigation run 2 hrs"

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 40)
    private ActivityType activityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private ActivityStatus status = ActivityStatus.PLANNED;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Column(name = "assigned_to", length = 100)
    private String assignedTo; // Name of field supervisor or worker

    @Column(name = "notes", length = 1000)
    private String notes;

    // Field on which this activity is executed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    // Optional crop associated with this activity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id")
    private Crop crop;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
