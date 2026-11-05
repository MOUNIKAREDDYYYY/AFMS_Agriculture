package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FarmRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * ============================================================================================
 * Phase 2: Service Layer - FarmService
 * ============================================================================================
 */
public interface FarmService {

    FarmResponseDTO createFarm(FarmRequestDTO requestDTO);

    FarmResponseDTO getFarmById(Long id);

    PagedResponse<FarmResponseDTO> getAllFarms(Pageable pageable);

    PagedResponse<FarmResponseDTO> getFarmsByFarmerId(Long farmerId, Pageable pageable);

    PagedResponse<FarmResponseDTO> searchFarms(String query, Pageable pageable);

    FarmResponseDTO updateFarm(Long id, FarmRequestDTO requestDTO);

    FarmResponseDTO patchFarm(Long id, Map<String, Object> updates);

    void deleteFarm(Long id);
}
