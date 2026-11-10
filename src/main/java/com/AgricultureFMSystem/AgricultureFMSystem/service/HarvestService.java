package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.HarvestRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.HarvestResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.HarvestSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 3: Service Layer - HarvestService (Harvest Records & Yield Aggregations)
 * ============================================================================================
 */
public interface HarvestService {

    HarvestResponseDTO recordHarvest(HarvestRequestDTO requestDTO);

    HarvestResponseDTO getHarvestById(Long id);

    PagedResponse<HarvestResponseDTO> getAllHarvests(Pageable pageable);

    PagedResponse<HarvestResponseDTO> getHarvestsByFilters(Long fieldId, Long cropId, Pageable pageable);

    PagedResponse<HarvestResponseDTO> searchHarvests(String query, Pageable pageable);

    PagedResponse<HarvestResponseDTO> getHarvestsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    HarvestResponseDTO updateHarvest(Long id, HarvestRequestDTO requestDTO);

    HarvestResponseDTO patchHarvest(Long id, Map<String, Object> updates);

    void deleteHarvest(Long id);

    // Business Reporting APIs
    List<HarvestSummaryDTO> getHarvestSummaryByCrop();

    List<HarvestSummaryDTO> getHarvestSummaryByCropAndDateRange(LocalDate startDate, LocalDate endDate);

    BigDecimal getTotalRevenueByFarm(Long farmId);
}
