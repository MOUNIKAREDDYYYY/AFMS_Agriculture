package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.CropPlanRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.CropPlanResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropPlanStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropSeason;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2: Service Layer - CropPlanService (Crop Planning APIs)
 * ============================================================================================
 */
public interface CropPlanService {

    CropPlanResponseDTO createCropPlan(CropPlanRequestDTO requestDTO);

    CropPlanResponseDTO getCropPlanById(Long id);

    PagedResponse<CropPlanResponseDTO> getAllCropPlans(Pageable pageable);

    PagedResponse<CropPlanResponseDTO> getCropPlansByFieldId(Long fieldId, Pageable pageable);

    PagedResponse<CropPlanResponseDTO> getCropPlansByStatus(CropPlanStatus status, Pageable pageable);

    PagedResponse<CropPlanResponseDTO> getCropPlansBySeason(CropSeason season, Pageable pageable);

    PagedResponse<CropPlanResponseDTO> getCropPlansByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    CropPlanResponseDTO updateCropPlan(Long id, CropPlanRequestDTO requestDTO);

    CropPlanResponseDTO patchCropPlan(Long id, Map<String, Object> updates);

    void deleteCropPlan(Long id);

    // Business API: Approve crop plan (/approve)
    CropPlanResponseDTO approveCropPlan(Long id);

    // Business API: Update crop plan status (/status)
    CropPlanResponseDTO updateCropPlanStatus(Long id, CropPlanStatus status);

    // Business API: Cancel crop plan (/cancel)
    CropPlanResponseDTO cancelCropPlan(Long id);
}
