package com.AgricultureFMSystem.AgricultureFMSystem.enums;

/**
 * Status of an agricultural crop plan.
 */
public enum CropPlanStatus {
    DRAFT,          // Initial plan created by farmer
    APPROVED,       // Approved by farm supervisor/agronomist
    IN_PROGRESS,    // Seeds planted and crop is currently growing
    COMPLETED,      // Crop harvested and cycle completed
    CANCELLED       // Plan dropped or cancelled
}
