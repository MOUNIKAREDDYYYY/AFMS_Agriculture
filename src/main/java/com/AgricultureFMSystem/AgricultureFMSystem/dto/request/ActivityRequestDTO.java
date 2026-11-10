package com.AgricultureFMSystem.AgricultureFMSystem.dto.request;

import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request payload for scheduling and updating field farming activities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRequestDTO {

    @NotBlank(message = "Activity title is required")
    @Size(min = 2, max = 150, message = "Title must be between 2 and 150 characters")
    private String title;

    @NotNull(message = "Activity type is required")
    private ActivityType activityType;

    private ActivityStatus status;

    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    private LocalDate completionDate;

    @Size(max = 100, message = "Assigned person name cannot exceed 100 characters")
    private String assignedTo;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @NotNull(message = "Field ID is required")
    private Long fieldId;

    private Long cropId; // Optional crop reference
}
