package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FarmRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.service.FarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 4: Farm REST Controller
 * ============================================================================================
 * Endpoints:
 * - POST   /api/farms        -> Create farm
 * - GET    /api/farms        -> List / filter by farmerId / paginate
 * - GET    /api/farms/{id}   -> Get by ID
 * - PUT    /api/farms/{id}   -> Full update
 * - PATCH  /api/farms/{id}   -> Partial update
 * - DELETE /api/farms/{id}   -> Delete farm
 * - GET    /api/farms/search -> Search by name or location
 */
@RestController
@RequestMapping("/api/farms")
@RequiredArgsConstructor
@Tag(name = "2. Farm Management", description = "Endpoints for managing Farms associated with Farmers")
public class FarmController {

    private final FarmService farmService;

    @PostMapping
    @Operation(summary = "Create a new farm", description = "Creates a farm linked to an existing Farmer.")
    @ApiResponse(responseCode = "201", description = "Farm created successfully")
    @ApiResponse(responseCode = "400", description = "Validation failure")
    @ApiResponse(responseCode = "404", description = "Farmer not found")
    public ResponseEntity<FarmResponseDTO> createFarm(@Valid @RequestBody FarmRequestDTO requestDTO) {
        return new ResponseEntity<>(farmService.createFarm(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all farms", description = "Returns a paginated list of farms with optional filtering by farmer ID.")
    public ResponseEntity<PagedResponse<FarmResponseDTO>> getAllFarms(
            @Parameter(description = "Filter farms owned by specific farmer ID")
            @RequestParam(required = false) Long farmerId,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        if (farmerId != null) {
            return ResponseEntity.ok(farmService.getFarmsByFarmerId(farmerId, pageable));
        }
        return ResponseEntity.ok(farmService.getAllFarms(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get farm by ID", description = "Fetches a farm by its unique identifier.")
    public ResponseEntity<FarmResponseDTO> getFarmById(@PathVariable Long id) {
        return ResponseEntity.ok(farmService.getFarmById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search farms", description = "Search farms by name or location keyword.")
    public ResponseEntity<PagedResponse<FarmResponseDTO>> searchFarms(
            @RequestParam String query,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(farmService.searchFarms(query, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Full update of a farm", description = "Replaces all details of an existing farm.")
    public ResponseEntity<FarmResponseDTO> updateFarm(
            @PathVariable Long id,
            @Valid @RequestBody FarmRequestDTO requestDTO) {

        return ResponseEntity.ok(farmService.updateFarm(id, requestDTO));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update of a farm", description = "Updates specific properties of a farm.")
    public ResponseEntity<FarmResponseDTO> patchFarm(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        return ResponseEntity.ok(farmService.patchFarm(id, updates));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete farm", description = "Deletes a farm along with its fields and expenses.")
    public ResponseEntity<Void> deleteFarm(@PathVariable Long id) {
        farmService.deleteFarm(id);
        return ResponseEntity.noContent().build();
    }
}
