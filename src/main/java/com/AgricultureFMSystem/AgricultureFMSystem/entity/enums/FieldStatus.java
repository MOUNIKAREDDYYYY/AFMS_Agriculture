package com.AgricultureFMSystem.AgricultureFMSystem.entity.enums;

/**
 * Lifecycle status of an agricultural field.
 */
public enum FieldStatus {
    AVAILABLE,      // Ready for ploughing or seeding
    PREPARATION,    // Soil preparation or tilling in progress
    PLANTED,        // Currently growing active crops
    FALLOW          // Resting period to restore soil fertility
}
