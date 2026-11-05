package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.CropRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.CropResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropCategory;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.CropSeason;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * ============================================================================================
 * Phase 2: Service Layer - CropService
 * ============================================================================================
 */
public interface CropService {

    CropResponseDTO createCrop(CropRequestDTO requestDTO);

    CropResponseDTO getCropById(Long id);

    PagedResponse<CropResponseDTO> getAllCrops(Pageable pageable);

    PagedResponse<CropResponseDTO> getCropsByCategory(CropCategory category, Pageable pageable);

    PagedResponse<CropResponseDTO> getCropsBySeason(CropSeason season, Pageable pageable);

    PagedResponse<CropResponseDTO> searchCrops(String query, Pageable pageable);

    CropResponseDTO updateCrop(Long id, CropRequestDTO requestDTO);

    CropResponseDTO patchCrop(Long id, Map<String, Object> updates);

    void deleteCrop(Long id);
}
