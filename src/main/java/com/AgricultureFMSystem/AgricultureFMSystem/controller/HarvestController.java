package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.HarvestRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.HarvestResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.HarvestSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.service.HarvestService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2, 3 & 4: Harvest REST Controller (Harvest Records & Yield Reporting)
 * ============================================================================================
 * Endpoints:
 * - POST   /api/harvests                         -> Record harvest
 * - GET    /api/harvests                         -> List / filter by field or crop
 * - GET    /api/harvests/{id}                    -> Get by ID
 * - PUT    /api/harvests/{id}                    -> Full update
 * - PATCH  /api/harvests/{id}                    -> Partial update
 * - DELETE /api/harvests/{id}                    -> Delete harvest
 * - GET    /api/harvests/date-range              -> Filter harvests by date
 * - GET    /api/harvests/report/by-crop          -> Aggregation: Yield and revenue grouped by crop
 * - GET    /api/harvests/report/by-crop-and-date -> Filtered yield aggregation
 * - GET    /api/harvests/report/farm-revenue     -> Total harvest revenue for a farm
 */
@Slf4j
@RestController
@RequestMapping("/api/harvests")
@RequiredArgsConstructor
@Tag(name = "8. Harvest Records & Yield Reports", description = "Endpoints for tracking crop yields, output quantities, and harvest revenue")
public class HarvestController {

    private final HarvestService harvestService;

    @PostMapping
    @Operation(summary = "Record a harvest", description = "Logs production yield from a field with quality grade and revenue.")
    public ResponseEntity<HarvestResponseDTO> recordHarvest(@Valid @RequestBody HarvestRequestDTO requestDTO) {
        log.info("REST request to log harvest: quantity={} {}", requestDTO.getQuantity(), requestDTO.getUnit());
        HarvestResponseDTO response = harvestService.recordHarvest(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List harvest records", description = "Returns paginated harvest records with optional field and crop filters.")
    public ResponseEntity<PagedResponse<HarvestResponseDTO>> getAllHarvests(
            @Parameter(description = "Filter by Field ID")
            @RequestParam(required = false) Long fieldId,
            @Parameter(description = "Filter by Crop ID")
            @RequestParam(required = false) Long cropId,
            @PageableDefault(page = 0, size = 10, sort = "harvestDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("REST request to list harvests: fieldId={}, cropId={}", fieldId, cropId);
        if (fieldId != null) {
            return ResponseEntity.ok(harvestService.getHarvestsByFieldId(fieldId, pageable));
        }
        if (cropId != null) {
            return ResponseEntity.ok(harvestService.getHarvestsByCropId(cropId, pageable));
        }
        return ResponseEntity.ok(harvestService.getAllHarvests(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get harvest by ID", description = "Fetches details of a harvest record.")
    public ResponseEntity<HarvestResponseDTO> getHarvestById(@PathVariable Long id) {
        log.debug("REST request to get harvest ID: {}", id);
        return ResponseEntity.ok(harvestService.getHarvestById(id));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Filter harvests by date range", description = "Returns harvest records within date interval.")
    public ResponseEntity<PagedResponse<HarvestResponseDTO>> getHarvestsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(page = 0, size = 10, sort = "harvestDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("REST request for harvests between {} and {}", startDate, endDate);
        return ResponseEntity.ok(harvestService.getHarvestsByDateRange(startDate, endDate, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Full update of a harvest record", description = "Replaces all details of an existing harvest entry.")
    public ResponseEntity<HarvestResponseDTO> updateHarvest(
            @PathVariable Long id,
            @Valid @RequestBody HarvestRequestDTO requestDTO) {

        log.info("REST request to update harvest ID: {}", id);
        return ResponseEntity.ok(harvestService.updateHarvest(id, requestDTO));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update of a harvest record", description = "Updates specific properties of a harvest record.")
    public ResponseEntity<HarvestResponseDTO> patchHarvest(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        log.info("REST request to patch harvest ID: {}", id);
        return ResponseEntity.ok(harvestService.patchHarvest(id, updates));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete harvest record", description = "Deletes a harvest entry.")
    public ResponseEntity<Void> deleteHarvest(@PathVariable Long id) {
        log.info("REST request to delete harvest ID: {}", id);
        harvestService.deleteHarvest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/report/by-crop")
    @Operation(summary = "Harvest summary by crop", description = "Business API: Aggregation query summing total harvested quantity and revenue per crop.")
    public ResponseEntity<List<HarvestSummaryDTO>> getHarvestSummaryByCrop() {
        log.info("REST request for aggregate harvest summary grouped by crop");
        return ResponseEntity.ok(harvestService.getHarvestSummaryByCrop());
    }

    @GetMapping("/report/by-crop-and-date")
    @Operation(summary = "Filtered harvest summary by crop", description = "Business API: Aggregates crop yield and revenue within a specified date interval.")
    public ResponseEntity<List<HarvestSummaryDTO>> getHarvestSummaryByCropAndDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("REST request for crop harvest summary between {} and {}", startDate, endDate);
        return ResponseEntity.ok(harvestService.getHarvestSummaryByCropAndDateRange(startDate, endDate));
    }

    @GetMapping("/report/farm-revenue")
    @Operation(summary = "Total harvest revenue for a farm", description = "Business API: Calculates total revenue reaped from all fields of a farm.")
    public ResponseEntity<BigDecimal> getTotalRevenueForFarm(@RequestParam Long farmId) {
        log.info("REST request for total revenue of farm ID: {}", farmId);
        return ResponseEntity.ok(harvestService.getTotalRevenueByFarm(farmId));
    }
}
