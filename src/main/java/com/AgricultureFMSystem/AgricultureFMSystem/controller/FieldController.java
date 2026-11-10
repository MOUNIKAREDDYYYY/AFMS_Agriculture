package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FieldRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FieldResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.FieldStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.service.FieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * Phase 2 & 4: Field REST Controller
 * ============================================================================================
 * Endpoints:
 * - POST   /api/fields                 -> Create field
 * - GET    /api/fields                 -> List / filter by farm or status
 * - GET    /api/fields/{id}            -> Get by ID
 * - PUT    /api/fields/{id}            -> Full update
 * - PATCH  /api/fields/{id}            -> Partial update
 * - DELETE /api/fields/{id}            -> Delete field
 * - GET    /api/fields/search          -> Search fields
 * - PATCH  /api/fields/{id}/status     -> Update field status (AVAILABLE, PLANTED, FALLOW, PREPARATION)
 * - POST   /api/fields/{id}/assign-crop -> Assign or reassign crop to field
 */
@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
@Tag(name = "3. Field Management", description = "Endpoints for managing agricultural Plots/Fields inside Farms")
public class FieldController {

    private final FieldService fieldService;

    @PostMapping
    @Operation(summary = "Create a new field", description = "Adds a field plot to a specified Farm.")
    public ResponseEntity<FieldResponseDTO> createField(@Valid @RequestBody FieldRequestDTO requestDTO) {
        return new ResponseEntity<>(fieldService.createField(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all fields", description = "Returns paginated fields with optional farm ID and status filters.")
    public ResponseEntity<PagedResponse<FieldResponseDTO>> getAllFields(
            @Parameter(description = "Filter by parent Farm ID")
            @RequestParam(required = false) Long farmId,
            @Parameter(description = "Filter by Field status (AVAILABLE, PLANTED, FALLOW, PREPARATION)")
            @RequestParam(required = false) FieldStatus status,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        if (farmId != null || status != null) {
            return ResponseEntity.ok(fieldService.getFieldsByFilters(farmId, status, pageable));
        }
        return ResponseEntity.ok(fieldService.getAllFields(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get field by ID", description = "Fetches details of a specific field plot.")
    public ResponseEntity<FieldResponseDTO> getFieldById(@PathVariable Long id) {
        return ResponseEntity.ok(fieldService.getFieldById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search fields", description = "Search fields by name, soil type, or irrigation type.")
    public ResponseEntity<PagedResponse<FieldResponseDTO>> searchFields(
            @RequestParam String query,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(fieldService.searchFields(query, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Full update of a field", description = "Replaces all details of an existing field.")
    public ResponseEntity<FieldResponseDTO> updateField(
            @PathVariable Long id,
            @Valid @RequestBody FieldRequestDTO requestDTO) {

        return ResponseEntity.ok(fieldService.updateField(id, requestDTO));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update of a field", description = "Updates specific properties of a field.")
    public ResponseEntity<FieldResponseDTO> patchField(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        return ResponseEntity.ok(fieldService.patchField(id, updates));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete field", description = "Removes a field from the database.")
    public ResponseEntity<Void> deleteField(@PathVariable Long id) {
        fieldService.deleteField(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update field status", description = "Updates field status to AVAILABLE, PREPARATION, PLANTED, or FALLOW.")
    public ResponseEntity<FieldResponseDTO> updateFieldStatus(
            @PathVariable Long id,
            @RequestParam FieldStatus status) {

        return ResponseEntity.ok(fieldService.updateFieldStatus(id, status));
    }

    @PostMapping("/{id}/assign-crop")
    @Operation(summary = "Assign or clear crop in field", description = "Assigns an active crop to the field or clears it.")
    public ResponseEntity<FieldResponseDTO> assignCrop(
            @PathVariable Long id,
            @RequestParam(required = false) Long cropId) {

        return ResponseEntity.ok(fieldService.assignCropToField(id, cropId));
    }
}
