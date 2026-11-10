package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response payload representing a field activity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponseDTO {

    private Long id;
    private String title;
    private ActivityType activityType;
    private ActivityStatus status;
    private LocalDate scheduledDate;
    private LocalDate completionDate;
    private String assignedTo;
    private String notes;
    private Long fieldId;
    private String fieldName;
    private Long cropId;
    private String cropName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
