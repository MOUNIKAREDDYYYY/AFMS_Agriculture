package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FarmerRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmerResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.service.FarmerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
 * Phase 2 & 4: Farmer REST Controller with OpenAPI & Bean Validation
 * ============================================================================================
 * Endpoints:
 * - POST   /api/farmers         -> Create farmer
 * - GET    /api/farmers         -> List / paginate / filter active status
 * - GET    /api/farmers/{id}    -> Get by ID
 * - PUT    /api/farmers/{id}    -> Full update
 * - PATCH  /api/farmers/{id}    -> Partial update
 * - DELETE /api/farmers/{id}    -> Delete farmer
 * - GET    /api/farmers/search  -> Search by name, email, or government ID
 * - PATCH  /api/farmers/{id}/status -> Toggle farmer active status
 */
@Slf4j
@RestController
@RequestMapping("/api/farmers")
@RequiredArgsConstructor
@Tag(name = "1. Farmer Management", description = "Endpoints for registering, managing, and searching Farmers")
public class FarmerController {

    private final FarmerService farmerService;

    @PostMapping
    @Operation(summary = "Create a new farmer", description = "Registers a new farmer with name, contact info, and email.")
    @ApiResponse(responseCode = "201", description = "Farmer registered successfully")
    @ApiResponse(responseCode = "400", description = "Validation failure")
    @ApiResponse(responseCode = "409", description = "Email already in use")
    public ResponseEntity<FarmerResponseDTO> createFarmer(@Valid @RequestBody FarmerRequestDTO requestDTO) {
        log.info("REST request to create farmer: {}", requestDTO.getEmail());
        FarmerResponseDTO response = farmerService.createFarmer(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all farmers", description = "Returns a paginated list of farmers with optional active-status filtering.")
    public ResponseEntity<PagedResponse<FarmerResponseDTO>> getAllFarmers(
            @Parameter(description = "Filter by active status (true/false)")
            @RequestParam(required = false) Boolean active,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("REST request to get farmers, active filter: {}", active);
        if (active != null) {
            return ResponseEntity.ok(farmerService.getFarmersByActiveStatus(active, pageable));
        }
        return ResponseEntity.ok(farmerService.getAllFarmers(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get farmer by ID", description = "Fetches a farmer by their unique identifier.")
    @ApiResponse(responseCode = "200", description = "Farmer found")
    @ApiResponse(responseCode = "404", description = "Farmer not found")
    public ResponseEntity<FarmerResponseDTO> getFarmerById(@PathVariable Long id) {
        log.debug("REST request to get farmer by ID: {}", id);
        return ResponseEntity.ok(farmerService.getFarmerById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search farmers", description = "Case-insensitive query across farmer names, emails, and ID numbers.")
    public ResponseEntity<PagedResponse<FarmerResponseDTO>> searchFarmers(
            @RequestParam String query,
            @PageableDefault(page = 0, size = 10, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {

        log.debug("REST search farmers with query: {}", query);
        return ResponseEntity.ok(farmerService.searchFarmers(query, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Full update of a farmer", description = "Replaces all editable fields of an existing farmer.")
    @ApiResponse(responseCode = "200", description = "Farmer updated successfully")
    @ApiResponse(responseCode = "404", description = "Farmer not found")
    public ResponseEntity<FarmerResponseDTO> updateFarmer(
            @PathVariable Long id,
            @Valid @RequestBody FarmerRequestDTO requestDTO) {

        log.info("REST request to update farmer ID: {}", id);
        return ResponseEntity.ok(farmerService.updateFarmer(id, requestDTO));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update of a farmer", description = "Updates specified fields of a farmer without overwriting others.")
    public ResponseEntity<FarmerResponseDTO> patchFarmer(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        log.info("REST request to patch farmer ID: {}", id);
        return ResponseEntity.ok(farmerService.patchFarmer(id, updates));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete farmer", description = "Deletes a farmer and cascades to delete all their associated farms.")
    @ApiResponse(responseCode = "204", description = "Farmer successfully deleted")
    @ApiResponse(responseCode = "404", description = "Farmer not found")
    public ResponseEntity<Void> deleteFarmer(@PathVariable Long id) {
        log.info("REST request to delete farmer ID: {}", id);
        farmerService.deleteFarmer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Toggle active status", description = "Toggles a farmer between active and inactive states.")
    public ResponseEntity<FarmerResponseDTO> toggleStatus(@PathVariable Long id) {
        log.info("REST request to toggle status for farmer ID: {}", id);
        return ResponseEntity.ok(farmerService.toggleFarmerStatus(id));
    }
}
