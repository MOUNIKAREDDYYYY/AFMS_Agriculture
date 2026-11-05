package com.AgricultureFMSystem.AgricultureFMSystem.entity.enums;

/**
 * Execution status of field farming activities.
 */
public enum ActivityStatus {
    PLANNED,        // Scheduled for a future date
    IN_PROGRESS,    // Currently being executed in the field
    COMPLETED,      // Successfully completed
    CANCELLED       // Cancelled due to weather or scheduling changes
}
