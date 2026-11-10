package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FieldRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FieldResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.FieldStatus;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * ============================================================================================
 * Phase 2: Service Layer - FieldService
 * ============================================================================================
 */
public interface FieldService {

    FieldResponseDTO createField(FieldRequestDTO requestDTO);

    FieldResponseDTO getFieldById(Long id);

    PagedResponse<FieldResponseDTO> getAllFields(Pageable pageable);

    PagedResponse<FieldResponseDTO> getFieldsByFilters(Long farmId, FieldStatus status, Pageable pageable);

    PagedResponse<FieldResponseDTO> searchFields(String query, Pageable pageable);

    FieldResponseDTO updateField(Long id, FieldRequestDTO requestDTO);

    FieldResponseDTO patchField(Long id, Map<String, Object> updates);

    void deleteField(Long id);

    // Business API: Update field status (AVAILABLE, PREPARATION, PLANTED, FALLOW)
    FieldResponseDTO updateFieldStatus(Long id, FieldStatus status);

    // Business API: Assign or change crop in a field
    FieldResponseDTO assignCropToField(Long fieldId, Long cropId);
}
