package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.HarvestRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.HarvestResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.HarvestSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Crop;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Field;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Harvest;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.HarvestMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.CropRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FieldRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.HarvestRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.service.HarvestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 3: Harvest Service Implementation with Aggregation Queries
 * ============================================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HarvestServiceImpl implements HarvestService {

    private final HarvestRepository harvestRepository;
    private final FieldRepository fieldRepository;
    private final CropRepository cropRepository;
    private final HarvestMapper harvestMapper;

    @Override
    @Transactional
    public HarvestResponseDTO recordHarvest(HarvestRequestDTO requestDTO) {
        log.info("Recording harvest of quantity {} {} for field ID {}",
                requestDTO.getQuantity(), requestDTO.getUnit(), requestDTO.getFieldId());

        Field field = fieldRepository.findById(requestDTO.getFieldId())
                .orElseThrow(() -> new ResourceNotFoundException("Field", "id", requestDTO.getFieldId()));

        Crop crop = cropRepository.findById(requestDTO.getCropId())
                .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", requestDTO.getCropId()));

        Harvest harvest = harvestMapper.toEntity(requestDTO, field, crop);
        Harvest saved = harvestRepository.save(harvest);
        return harvestMapper.toResponseDTO(saved);
    }

    @Override
    public HarvestResponseDTO getHarvestById(Long id) {
        log.debug("Fetching harvest by ID: {}", id);
        Harvest harvest = harvestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest", "id", id));
        return harvestMapper.toResponseDTO(harvest);
    }

    @Override
    public PagedResponse<HarvestResponseDTO> getAllHarvests(Pageable pageable) {
        log.debug("Fetching all harvests with pagination");
        Page<HarvestResponseDTO> page = harvestRepository.findAll(pageable)
                .map(harvestMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<HarvestResponseDTO> getHarvestsByFieldId(Long fieldId, Pageable pageable) {
        log.debug("Fetching harvests for field ID: {}", fieldId);
        if (!fieldRepository.existsById(fieldId)) {
            throw new ResourceNotFoundException("Field", "id", fieldId);
        }
        Page<HarvestResponseDTO> page = harvestRepository.findByFieldId(fieldId, pageable)
                .map(harvestMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<HarvestResponseDTO> getHarvestsByCropId(Long cropId, Pageable pageable) {
        log.debug("Fetching harvests for crop ID: {}", cropId);
        if (!cropRepository.existsById(cropId)) {
            throw new ResourceNotFoundException("Crop", "id", cropId);
        }
        Page<HarvestResponseDTO> page = harvestRepository.findByCropId(cropId, pageable)
                .map(harvestMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<HarvestResponseDTO> getHarvestsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.debug("Fetching harvests between {} and {}", startDate, endDate);
        Page<HarvestResponseDTO> page = harvestRepository.findByHarvestDateBetween(startDate, endDate, pageable)
                .map(harvestMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional
    public HarvestResponseDTO updateHarvest(Long id, HarvestRequestDTO requestDTO) {
        log.info("Updating harvest ID: {}", id);
        Harvest existing = harvestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest", "id", id));

        Field field = null;
        if (requestDTO.getFieldId() != null) {
            field = fieldRepository.findById(requestDTO.getFieldId())
                    .orElseThrow(() -> new ResourceNotFoundException("Field", "id", requestDTO.getFieldId()));
        }

        Crop crop = null;
        if (requestDTO.getCropId() != null) {
            crop = cropRepository.findById(requestDTO.getCropId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", requestDTO.getCropId()));
        }

        harvestMapper.updateEntityFromDTO(requestDTO, existing, field, crop);
        Harvest saved = harvestRepository.save(existing);
        return harvestMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public HarvestResponseDTO patchHarvest(Long id, Map<String, Object> updates) {
        log.info("Applying partial updates to harvest ID: {}", id);
        Harvest existing = harvestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest", "id", id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "harvestDate" -> existing.setHarvestDate(LocalDate.parse(value.toString()));
                case "quantity" -> existing.setQuantity(Double.parseDouble(value.toString()));
                case "unit" -> existing.setUnit((String) value);
                case "qualityGrade" -> existing.setQualityGrade((String) value);
                case "unitPrice" -> existing.setUnitPrice(new BigDecimal(value.toString()));
                case "totalRevenue" -> existing.setTotalRevenue(new BigDecimal(value.toString()));
                case "storageLocation" -> existing.setStorageLocation((String) value);
                case "notes" -> existing.setNotes((String) value);
            }
        });

        Harvest saved = harvestRepository.save(existing);
        return harvestMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteHarvest(Long id) {
        log.info("Deleting harvest ID: {}", id);
        Harvest harvest = harvestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest", "id", id));
        harvestRepository.delete(harvest);
    }

    @Override
    public List<HarvestSummaryDTO> getHarvestSummaryByCrop() {
        log.info("Generating aggregate harvest yield summary by crop");
        return harvestRepository.aggregateHarvestsByCrop();
    }

    @Override
    public List<HarvestSummaryDTO> getHarvestSummaryByCropAndDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Generating aggregate harvest summary by crop between {} and {}", startDate, endDate);
        return harvestRepository.aggregateHarvestsByCropAndDateRange(startDate, endDate);
    }

    @Override
    public BigDecimal getTotalRevenueByFarm(Long farmId) {
        log.debug("Calculating total harvest revenue for farm ID: {}", farmId);
        return harvestRepository.sumHarvestRevenueByFarmId(farmId);
    }
}
