package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.CropRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.CropResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropCategory;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropSeason;
import com.AgricultureFMSystem.AgricultureFMSystem.service.CropService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 4: Crop REST Controller
 * ============================================================================================
 * Endpoints:
 * - POST   /api/crops        -> Register new crop
 * - GET    /api/crops        -> List / filter by category or season
 * - GET    /api/crops/{id}   -> Get by ID
 * - PUT    /api/crops/{id}   -> Full update
 * - PATCH  /api/crops/{id}   -> Partial update
 * - DELETE /api/crops/{id}   -> Delete crop
 * - GET    /api/crops/search -> Search crops by name or variety
 */
@Slf4j
@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
@Tag(name = "4. Crop Management", description = "Endpoints for defining crops, varieties, and agronomic guidelines")
public class CropController {

    private final CropService cropService;

    @PostMapping
    @Operation(summary = "Register a new crop", description = "Adds a crop with growth duration and climate requirements.")
    public ResponseEntity<CropResponseDTO> createCrop(@Valid @RequestBody CropRequestDTO requestDTO) {
        log.info("REST request to create crop: {}", requestDTO.getName());
        CropResponseDTO response = cropService.createCrop(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all crops", description = "Returns paginated crops with optional category and season filters.")
    public ResponseEntity<PagedResponse<CropResponseDTO>> getAllCrops(
            @Parameter(description = "Filter by category (GRAIN, VEGETABLE, PULSE, etc.)")
            @RequestParam(required = false) CropCategory category,
            @Parameter(description = "Filter by season (KHARIF, RABI, ZAID, YEAR_ROUND)")
            @RequestParam(required = false) CropSeason season,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.debug("REST request to list crops: category={}, season={}", category, season);
        if (category != null) {
            return ResponseEntity.ok(cropService.getCropsByCategory(category, pageable));
        }
        if (season != null) {
            return ResponseEntity.ok(cropService.getCropsBySeason(season, pageable));
        }
        return ResponseEntity.ok(cropService.getAllCrops(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get crop by ID", description = "Fetches a crop specification by its unique identifier.")
    public ResponseEntity<CropResponseDTO> getCropById(@PathVariable Long id) {
        log.debug("REST request to get crop ID: {}", id);
        return ResponseEntity.ok(cropService.getCropById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search crops", description = "Search crops by name or variety.")
    public ResponseEntity<PagedResponse<CropResponseDTO>> searchCrops(
            @RequestParam String query,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.debug("REST search crops with query: {}", query);
        return ResponseEntity.ok(cropService.searchCrops(query, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Full update of a crop", description = "Replaces all details of an existing crop.")
    public ResponseEntity<CropResponseDTO> updateCrop(
            @PathVariable Long id,
            @Valid @RequestBody CropRequestDTO requestDTO) {

        log.info("REST request to update crop ID: {}", id);
        return ResponseEntity.ok(cropService.updateCrop(id, requestDTO));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update of a crop", description = "Updates specific properties of a crop.")
    public ResponseEntity<CropResponseDTO> patchCrop(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        log.info("REST request to patch crop ID: {}", id);
        return ResponseEntity.ok(cropService.patchCrop(id, updates));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete crop", description = "Deletes a crop specification.")
    public ResponseEntity<Void> deleteCrop(@PathVariable Long id) {
        log.info("REST request to delete crop ID: {}", id);
        cropService.deleteCrop(id);
        return ResponseEntity.noContent().build();
    }
}
