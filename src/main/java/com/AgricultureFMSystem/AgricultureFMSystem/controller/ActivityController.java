package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.ActivityRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ActivityResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityType;
import com.AgricultureFMSystem.AgricultureFMSystem.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 4: Activity Tracking REST Controller (Activity Tracking APIs)
 * ============================================================================================
 * Endpoints:
 * - POST   /api/activities                 -> Schedule new activity
 * - GET    /api/activities                 -> List / filter by field, status, type
 * - GET    /api/activities/{id}            -> Get by ID
 * - PUT    /api/activities/{id}            -> Full update
 * - PATCH  /api/activities/{id}            -> Partial update
 * - DELETE /api/activities/{id}            -> Delete activity
 * - GET    /api/activities/search          -> Search activities
 * - GET    /api/activities/pending         -> List pending activities
 * - GET    /api/activities/date-range      -> Filter by scheduled date range
 * - POST   /api/activities/{id}/complete   -> Complete activity
 * - PATCH  /api/activities/{id}/status     -> Update activity status
 * - POST   /api/activities/{id}/cancel     -> Cancel activity
 */
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@Tag(name = "6. Activity Tracking", description = "Endpoints for scheduling, assigning, and tracking field farming activities")
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    @Operation(summary = "Schedule field activity", description = "Schedules an agricultural operation on a field.")
    public ResponseEntity<ActivityResponseDTO> createActivity(@Valid @RequestBody ActivityRequestDTO requestDTO) {
        return new ResponseEntity<>(activityService.createActivity(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List activities", description = "Returns paginated activities with optional field ID, status, and type filters.")
    public ResponseEntity<PagedResponse<ActivityResponseDTO>> getAllActivities(
            @Parameter(description = "Filter by field ID")
            @RequestParam(required = false) Long fieldId,
            @Parameter(description = "Filter by status (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED)")
            @RequestParam(required = false) ActivityStatus status,
            @Parameter(description = "Filter by type (PLANTING, IRRIGATION, FERTILIZING, etc.)")
            @RequestParam(required = false) ActivityType type,
            @PageableDefault(page = 0, size = 10, sort = "scheduledDate", direction = Sort.Direction.ASC) Pageable pageable) {

        if (fieldId != null || status != null || type != null) {
            return ResponseEntity.ok(activityService.getActivitiesByFilters(fieldId, status, type, pageable));
        }
        return ResponseEntity.ok(activityService.getAllActivities(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get activity by ID", description = "Fetches activity details by ID.")
    public ResponseEntity<ActivityResponseDTO> getActivityById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getActivityById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search activities", description = "Search activities by title, notes, or assigned personnel.")
    public ResponseEntity<PagedResponse<ActivityResponseDTO>> searchActivities(
            @RequestParam String query,
            @PageableDefault(page = 0, size = 10, sort = "scheduledDate", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(activityService.searchActivities(query, pageable));
    }

    @GetMapping("/pending")
    @Operation(summary = "List pending activities", description = "Retrieves all activities in PLANNED or IN_PROGRESS status.")
    public ResponseEntity<List<ActivityResponseDTO>> getPendingActivities() {
        return ResponseEntity.ok(activityService.getPendingActivities());
    }

    @GetMapping("/date-range")
    @Operation(summary = "Filter activities by date range", description = "Retrieves activities scheduled between two dates.")
    public ResponseEntity<PagedResponse<ActivityResponseDTO>> getActivitiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(page = 0, size = 10, sort = "scheduledDate", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(activityService.getActivitiesByDateRange(startDate, endDate, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Full update of an activity", description = "Replaces all details of an existing activity.")
    public ResponseEntity<ActivityResponseDTO> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityRequestDTO requestDTO) {

        return ResponseEntity.ok(activityService.updateActivity(id, requestDTO));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update of an activity", description = "Updates specific properties of an activity.")
    public ResponseEntity<ActivityResponseDTO> patchActivity(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        return ResponseEntity.ok(activityService.patchActivity(id, updates));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete activity", description = "Deletes an activity from the schedule.")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Mark activity as completed", description = "Business API: Marks activity completed and sets completion date.")
    public ResponseEntity<ActivityResponseDTO> completeActivity(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completionDate,
            @RequestParam(required = false) String notes) {

        return ResponseEntity.ok(activityService.completeActivity(id, completionDate, notes));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update activity status", description = "Business API: Updates status to PLANNED, IN_PROGRESS, COMPLETED, or CANCELLED.")
    public ResponseEntity<ActivityResponseDTO> updateActivityStatus(
            @PathVariable Long id,
            @RequestParam ActivityStatus status) {

        return ResponseEntity.ok(activityService.updateActivityStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel activity", description = "Business API: Marks an activity as CANCELLED.")
    public ResponseEntity<ActivityResponseDTO> cancelActivity(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.cancelActivity(id));
    }
}
