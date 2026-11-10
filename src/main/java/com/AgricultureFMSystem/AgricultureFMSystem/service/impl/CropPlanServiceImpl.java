package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.CropPlanRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.CropPlanResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Crop;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.CropPlan;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Field;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropPlanStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropSeason;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.BadRequestException;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.CropPlanMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.CropPlanRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.CropRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FieldRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.service.CropPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 3: CropPlan Service Implementation (Crop Planning Management)
 * ============================================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CropPlanServiceImpl implements CropPlanService {

    private final CropPlanRepository cropPlanRepository;
    private final FieldRepository fieldRepository;
    private final CropRepository cropRepository;
    private final CropPlanMapper cropPlanMapper;

    @Override
    @Transactional
    @CacheEvict(value = "fieldHistory", allEntries = true)
    public CropPlanResponseDTO createCropPlan(CropPlanRequestDTO requestDTO) {
        log.info("Creating crop plan for field ID {} and crop ID {}", requestDTO.getFieldId(), requestDTO.getCropId());

        validateDates(requestDTO.getPlannedStartDate(), requestDTO.getEstimatedHarvestDate());

        Field field = fieldRepository.findById(requestDTO.getFieldId())
                .orElseThrow(() -> new ResourceNotFoundException("Field", "id", requestDTO.getFieldId()));

        Crop crop = cropRepository.findById(requestDTO.getCropId())
                .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", requestDTO.getCropId()));

        CropPlan plan = cropPlanMapper.toEntity(requestDTO, field, crop);
        CropPlan saved = cropPlanRepository.save(plan);
        return cropPlanMapper.toResponseDTO(saved);
    }

    @Override
    public CropPlanResponseDTO getCropPlanById(Long id) {
        log.debug("Fetching crop plan by ID: {}", id);
        CropPlan plan = cropPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CropPlan", "id", id));
        return cropPlanMapper.toResponseDTO(plan);
    }

    @Override
    public PagedResponse<CropPlanResponseDTO> getAllCropPlans(Pageable pageable) {
        log.debug("Fetching all crop plans with pagination");
        Page<CropPlanResponseDTO> page = cropPlanRepository.findAll(pageable)
                .map(cropPlanMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<CropPlanResponseDTO> getCropPlansByFilters(Long fieldId, CropPlanStatus status, CropSeason season, Pageable pageable) {
        log.debug("Fetching crop plans with filters: fieldId={}, status={}, season={}", fieldId, status, season);
        if (fieldId != null && !fieldRepository.existsById(fieldId)) {
            throw new ResourceNotFoundException("Field", "id", fieldId);
        }
        Page<CropPlanResponseDTO> page = cropPlanRepository.findByFilters(fieldId, status, season, pageable)
                .map(cropPlanMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<CropPlanResponseDTO> searchCropPlans(String query, Pageable pageable) {
        log.debug("Searching crop plans with query: '{}'", query);
        Page<CropPlanResponseDTO> page = cropPlanRepository.searchCropPlans(query, pageable)
                .map(cropPlanMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<CropPlanResponseDTO> getCropPlansByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.debug("Fetching crop plans between {} and {}", startDate, endDate);
        Page<CropPlanResponseDTO> page = cropPlanRepository.findPlansByDateRange(startDate, endDate, pageable)
                .map(cropPlanMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional
    @CacheEvict(value = "fieldHistory", allEntries = true)
    public CropPlanResponseDTO updateCropPlan(Long id, CropPlanRequestDTO requestDTO) {
        log.info("Updating crop plan ID: {}", id);
        validateDates(requestDTO.getPlannedStartDate(), requestDTO.getEstimatedHarvestDate());

        CropPlan existing = cropPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CropPlan", "id", id));

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

        cropPlanMapper.updateEntityFromDTO(requestDTO, existing, field, crop);
        CropPlan saved = cropPlanRepository.save(existing);
        return cropPlanMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "fieldHistory", allEntries = true)
    public CropPlanResponseDTO patchCropPlan(Long id, Map<String, Object> updates) {
        log.info("Applying partial updates to crop plan ID: {}", id);
        CropPlan existing = cropPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CropPlan", "id", id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "season" -> existing.setSeason(PatchHelper.requireEnum("season", value, CropSeason.class));
                case "plannedStartDate" -> existing.setPlannedStartDate(PatchHelper.requireLocalDate("plannedStartDate", value));
                case "estimatedHarvestDate" -> existing.setEstimatedHarvestDate(PatchHelper.requireLocalDate("estimatedHarvestDate", value));
                case "expectedYieldQuantity" -> existing.setExpectedYieldQuantity(
                        value == null ? null : PatchHelper.requireDouble("expectedYieldQuantity", value, true));
                case "yieldUnit" -> existing.setYieldUnit(PatchHelper.optionalString("yieldUnit", value));
                case "estimatedBudget" -> existing.setEstimatedBudget(
                        value == null ? null : PatchHelper.requireBigDecimal("estimatedBudget", value, true));
                case "status" -> existing.setStatus(PatchHelper.requireEnum("status", value, CropPlanStatus.class));
                case "notes" -> existing.setNotes(PatchHelper.optionalString("notes", value));
            }
        });

        validateDates(existing.getPlannedStartDate(), existing.getEstimatedHarvestDate());

        CropPlan saved = cropPlanRepository.save(existing);
        return cropPlanMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "fieldHistory", allEntries = true)
    public void deleteCropPlan(Long id) {
        log.info("Deleting crop plan ID: {}", id);
        CropPlan plan = cropPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CropPlan", "id", id));
        cropPlanRepository.delete(plan);
    }

    @Override
    @Transactional
    @CacheEvict(value = "fieldHistory", allEntries = true)
    public CropPlanResponseDTO approveCropPlan(Long id) {
        log.info("Approving crop plan ID: {}", id);
        CropPlan plan = cropPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CropPlan", "id", id));
        plan.setStatus(CropPlanStatus.APPROVED);
        CropPlan saved = cropPlanRepository.save(plan);
        return cropPlanMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "fieldHistory", allEntries = true)
    public CropPlanResponseDTO updateCropPlanStatus(Long id, CropPlanStatus status) {
        log.info("Setting status of crop plan {} to {}", id, status);
        CropPlan plan = cropPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CropPlan", "id", id));
        plan.setStatus(status);
        CropPlan saved = cropPlanRepository.save(plan);
        return cropPlanMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "fieldHistory", allEntries = true)
    public CropPlanResponseDTO cancelCropPlan(Long id) {
        log.info("Cancelling crop plan ID: {}", id);
        CropPlan plan = cropPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CropPlan", "id", id));
        plan.setStatus(CropPlanStatus.CANCELLED);
        CropPlan saved = cropPlanRepository.save(plan);
        return cropPlanMapper.toResponseDTO(saved);
    }

    private void validateDates(LocalDate startDate, LocalDate harvestDate) {
        if (startDate != null && harvestDate != null && harvestDate.isBefore(startDate)) {
            throw new BadRequestException("Estimated harvest date cannot be before planned start date");
        }
    }
}
