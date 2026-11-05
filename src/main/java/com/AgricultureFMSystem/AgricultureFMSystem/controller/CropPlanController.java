package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.CropPlanRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.CropPlanResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropPlanStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropSeason;
import com.AgricultureFMSystem.AgricultureFMSystem.service.CropPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 4: Crop Planning REST Controller (Crop Planning APIs)
 * ============================================================================================
 * Endpoints:
 * - POST   /api/crop-plans             -> Create crop plan
 * - GET    /api/crop-plans             -> List plans / filter by field, status, season
 * - GET    /api/crop-plans/{id}        -> Get plan by ID
 * - PUT    /api/crop-plans/{id}        -> Full update
 * - PATCH  /api/crop-plans/{id}        -> Partial update
 * - DELETE /api/crop-plans/{id}        -> Delete plan
 * - GET    /api/crop-plans/date-range  -> Date filtering on planned start dates
 * - POST   /api/crop-plans/{id}/approve -> Business API: Approve plan
 * - PATCH  /api/crop-plans/{id}/status  -> Business API: Update plan status
 * - POST   /api/crop-plans/{id}/cancel  -> Business API: Cancel plan
 */
@Slf4j
@RestController
@RequestMapping("/api/crop-plans")
@RequiredArgsConstructor
@Tag(name = "5. Crop Planning", description = "Endpoints for scheduling, budgeting, and approving crop planting cycles")
public class CropPlanController {

    private final CropPlanService cropPlanService;

    @PostMapping
    @Operation(summary = "Create a crop plan", description = "Prepares a planned crop cycle for a field with budget and yield expectations.")
    public ResponseEntity<CropPlanResponseDTO> createCropPlan(@Valid @RequestBody CropPlanRequestDTO requestDTO) {
        log.info("REST request to create crop plan for field ID: {}", requestDTO.getFieldId());
        CropPlanResponseDTO response = cropPlanService.createCropPlan(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List crop plans", description = "Returns paginated crop plans with optional filtering by field, status, or season.")
    public ResponseEntity<PagedResponse<CropPlanResponseDTO>> getAllCropPlans(
            @Parameter(description = "Filter by field ID")
            @RequestParam(required = false) Long fieldId,
            @Parameter(description = "Filter by status (DRAFT, APPROVED, IN_PROGRESS, COMPLETED, CANCELLED)")
            @RequestParam(required = false) CropPlanStatus status,
            @Parameter(description = "Filter by season (KHARIF, RABI, ZAID, YEAR_ROUND)")
            @RequestParam(required = false) CropSeason season,
            @PageableDefault(page = 0, size = 10, sort = "plannedStartDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("REST request to list crop plans: fieldId={}, status={}, season={}", fieldId, status, season);
        if (fieldId != null) {
            return ResponseEntity.ok(cropPlanService.getCropPlansByFieldId(fieldId, pageable));
        }
        if (status != null) {
            return ResponseEntity.ok(cropPlanService.getCropPlansByStatus(status, pageable));
        }
        if (season != null) {
            return ResponseEntity.ok(cropPlanService.getCropPlansBySeason(season, pageable));
        }
        return ResponseEntity.ok(cropPlanService.getAllCropPlans(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get crop plan by ID", description = "Fetches a crop plan by its unique ID.")
    public ResponseEntity<CropPlanResponseDTO> getCropPlanById(@PathVariable Long id) {
        log.debug("REST request to get crop plan ID: {}", id);
        return ResponseEntity.ok(cropPlanService.getCropPlanById(id));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Filter crop plans by date range", description = "Retrieves crop plans scheduled to start within date interval.")
    public ResponseEntity<PagedResponse<CropPlanResponseDTO>> getPlansByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(page = 0, size = 10, sort = "plannedStartDate", direction = Sort.Direction.ASC) Pageable pageable) {

        log.debug("REST request for crop plans between {} and {}", startDate, endDate);
        return ResponseEntity.ok(cropPlanService.getCropPlansByDateRange(startDate, endDate, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Full update of a crop plan", description = "Replaces all details of an existing crop plan.")
    public ResponseEntity<CropPlanResponseDTO> updateCropPlan(
            @PathVariable Long id,
            @Valid @RequestBody CropPlanRequestDTO requestDTO) {

        log.info("REST request to update crop plan ID: {}", id);
        return ResponseEntity.ok(cropPlanService.updateCropPlan(id, requestDTO));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update of a crop plan", description = "Updates specific properties of a crop plan.")
    public ResponseEntity<CropPlanResponseDTO> patchCropPlan(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        log.info("REST request to patch crop plan ID: {}", id);
        return ResponseEntity.ok(cropPlanService.patchCropPlan(id, updates));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete crop plan", description = "Deletes a crop plan from the system.")
    public ResponseEntity<Void> deleteCropPlan(@PathVariable Long id) {
        log.info("REST request to delete crop plan ID: {}", id);
        cropPlanService.deleteCropPlan(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve crop plan", description = "Business API: Marks a draft crop plan as APPROVED.")
    public ResponseEntity<CropPlanResponseDTO> approveCropPlan(@PathVariable Long id) {
        log.info("REST request to approve crop plan ID: {}", id);
        return ResponseEntity.ok(cropPlanService.approveCropPlan(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update crop plan status", description = "Business API: Changes the plan status (e.g., IN_PROGRESS, COMPLETED).")
    public ResponseEntity<CropPlanResponseDTO> updatePlanStatus(
            @PathVariable Long id,
            @RequestParam CropPlanStatus status) {

        log.info("REST request to update status of crop plan {} to {}", id, status);
        return ResponseEntity.ok(cropPlanService.updateCropPlanStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel crop plan", description = "Business API: Marks a crop plan as CANCELLED.")
    public ResponseEntity<CropPlanResponseDTO> cancelCropPlan(@PathVariable Long id) {
        log.info("REST request to cancel crop plan ID: {}", id);
        return ResponseEntity.ok(cropPlanService.cancelCropPlan(id));
    }
}
