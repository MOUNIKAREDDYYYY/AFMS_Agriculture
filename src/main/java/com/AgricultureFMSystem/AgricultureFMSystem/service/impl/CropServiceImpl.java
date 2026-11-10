package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.CropRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.CropResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Crop;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropCategory;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropSeason;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.CropMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.CropRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.service.CropService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 3: Crop Service Implementation
 * ============================================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CropServiceImpl implements CropService {

    private final CropRepository cropRepository;
    private final CropMapper cropMapper;

    // Evict all entries in "crops" cache region when a new crop is registered
    @Override
    @Transactional
    @CacheEvict(value = "crops", allEntries = true)
    public CropResponseDTO createCrop(CropRequestDTO requestDTO) {
        log.info("Registering crop: {}", requestDTO.getName());
        Crop crop = cropMapper.toEntity(requestDTO);
        Crop saved = cropRepository.save(crop);
        return cropMapper.toResponseDTO(saved);
    }

    // Cache hit will bypass database query; Cache miss queries database and caches response by ID
    @Override
    @Cacheable(value = "crops", key = "#id")
    public CropResponseDTO getCropById(Long id) {
        log.debug("Fetching crop by ID (Database Lookup): {}", id);
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", id));
        return cropMapper.toResponseDTO(crop);
    }

    @Override
    public PagedResponse<CropResponseDTO> getAllCrops(Pageable pageable) {
        log.debug("Fetching all crops with pagination");
        Page<CropResponseDTO> page = cropRepository.findAll(pageable)
                .map(cropMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<CropResponseDTO> getCropsByFilters(CropCategory category, CropSeason season, Pageable pageable) {
        log.debug("Fetching crops with filters: category={}, season={}", category, season);
        Page<CropResponseDTO> page = cropRepository.findByFilters(category, season, pageable)
                .map(cropMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<CropResponseDTO> searchCrops(String query, Pageable pageable) {
        log.debug("Searching crops with query: {}", query);
        Page<CropResponseDTO> page = cropRepository.searchCrops(query, pageable)
                .map(cropMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    // Evicts stale cache upon full update
    @Override
    @Transactional
    @CacheEvict(value = "crops", allEntries = true)
    public CropResponseDTO updateCrop(Long id, CropRequestDTO requestDTO) {
        log.info("Updating crop ID: {}", id);
        Crop existing = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", id));

        cropMapper.updateEntityFromDTO(requestDTO, existing);
        Crop updated = cropRepository.save(existing);
        return cropMapper.toResponseDTO(updated);
    }

    // Evicts stale cache upon partial patch
    @Override
    @Transactional
    @CacheEvict(value = "crops", allEntries = true)
    public CropResponseDTO patchCrop(Long id, Map<String, Object> updates) {
        log.info("Applying partial updates to crop ID: {}", id);
        Crop existing = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> existing.setName(PatchHelper.requireNonBlankString("name", value));
                case "variety" -> existing.setVariety(PatchHelper.optionalString("variety", value));
                case "category" -> existing.setCategory(PatchHelper.requireEnum("category", value, CropCategory.class));
                case "season" -> existing.setSeason(PatchHelper.requireEnum("season", value, CropSeason.class));
                case "growthDurationDays" -> existing.setGrowthDurationDays(PatchHelper.requireInteger("growthDurationDays", value, true));
                case "optimalTemperature" -> existing.setOptimalTemperature(PatchHelper.optionalString("optimalTemperature", value));
                case "waterRequirement" -> existing.setWaterRequirement(PatchHelper.optionalString("waterRequirement", value));
                case "standardYieldPerAcre" -> existing.setStandardYieldPerAcre(
                        value == null ? null : PatchHelper.requireDouble("standardYieldPerAcre", value, true));
                case "yieldUnit" -> existing.setYieldUnit(PatchHelper.optionalString("yieldUnit", value));
            }
        });

        Crop saved = cropRepository.save(existing);
        return cropMapper.toResponseDTO(saved);
    }

    // Evicts cached crop upon deletion
    @Override
    @Transactional
    @CacheEvict(value = "crops", allEntries = true)
    public void deleteCrop(Long id) {
        log.info("Deleting crop ID: {}", id);
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", id));
        cropRepository.delete(crop);
    }
}