package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.ActivityRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ActivityResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2: Service Layer - ActivityService (Activity Tracking APIs)
 * ============================================================================================
 */
public interface ActivityService {

    ActivityResponseDTO createActivity(ActivityRequestDTO requestDTO);

    ActivityResponseDTO getActivityById(Long id);

    PagedResponse<ActivityResponseDTO> getAllActivities(Pageable pageable);

    PagedResponse<ActivityResponseDTO> getActivitiesByFilters(Long fieldId, ActivityStatus status, ActivityType activityType, Pageable pageable);

    PagedResponse<ActivityResponseDTO> getActivitiesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    PagedResponse<ActivityResponseDTO> searchActivities(String query, Pageable pageable);

    List<ActivityResponseDTO> getPendingActivities();

    ActivityResponseDTO updateActivity(Long id, ActivityRequestDTO requestDTO);

    ActivityResponseDTO patchActivity(Long id, Map<String, Object> updates);

    void deleteActivity(Long id);

    // Business API: Mark activity as completed
    ActivityResponseDTO completeActivity(Long id, LocalDate completionDate, String notes);

    // Business API: Update activity status
    ActivityResponseDTO updateActivityStatus(Long id, ActivityStatus status);

    // Business API: Cancel activity
    ActivityResponseDTO cancelActivity(Long id);
}
