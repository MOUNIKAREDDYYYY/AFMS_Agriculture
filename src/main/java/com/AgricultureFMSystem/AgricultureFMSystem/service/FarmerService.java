package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FarmerRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmerResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * ============================================================================================
 * Phase 2: Service Layer & Transaction Boundaries - FarmerService
 * ============================================================================================
 */
public interface FarmerService {

    FarmerResponseDTO createFarmer(FarmerRequestDTO requestDTO);

    FarmerResponseDTO getFarmerById(Long id);

    PagedResponse<FarmerResponseDTO> getAllFarmers(Pageable pageable);

    PagedResponse<FarmerResponseDTO> getFarmersByActiveStatus(Boolean active, Pageable pageable);

    PagedResponse<FarmerResponseDTO> searchFarmers(String query, Pageable pageable);

    FarmerResponseDTO updateFarmer(Long id, FarmerRequestDTO requestDTO);

    FarmerResponseDTO patchFarmer(Long id, Map<String, Object> updates);

    void deleteFarmer(Long id);

    // Business API: Toggle active status
    FarmerResponseDTO toggleFarmerStatus(Long id);
}
