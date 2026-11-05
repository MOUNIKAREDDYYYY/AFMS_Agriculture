package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FieldRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FieldResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Crop;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farm;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Field;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.FieldStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.FieldMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.CropRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FarmRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FieldRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.service.FieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 3: Field Service Implementation
 * ============================================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final FarmRepository farmRepository;
    private final CropRepository cropRepository;
    private final FieldMapper fieldMapper;

    @Override
    @Transactional
    public FieldResponseDTO createField(FieldRequestDTO requestDTO) {
        log.info("Creating new field '{}' in farm ID {}", requestDTO.getName(), requestDTO.getFarmId());

        Farm farm = farmRepository.findById(requestDTO.getFarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm", "id", requestDTO.getFarmId()));

        Crop crop = null;
        if (requestDTO.getCurrentCropId() != null) {
            crop = cropRepository.findById(requestDTO.getCurrentCropId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", requestDTO.getCurrentCropId()));
        }

        Field field = fieldMapper.toEntity(requestDTO, farm, crop);
        Field saved = fieldRepository.save(field);
        return fieldMapper.toResponseDTO(saved);
    }

    @Override
    public FieldResponseDTO getFieldById(Long id) {
        log.debug("Fetching field by ID: {}", id);
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field", "id", id));
        return fieldMapper.toResponseDTO(field);
    }

    @Override
    public PagedResponse<FieldResponseDTO> getAllFields(Pageable pageable) {
        log.debug("Fetching paginated fields");
        Page<FieldResponseDTO> page = fieldRepository.findAll(pageable)
                .map(fieldMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<FieldResponseDTO> getFieldsByFarmId(Long farmId, Pageable pageable) {
        log.debug("Fetching fields for farm ID: {}", farmId);
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Farm", "id", farmId);
        }
        Page<FieldResponseDTO> page = fieldRepository.findByFarmId(farmId, pageable)
                .map(fieldMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<FieldResponseDTO> getFieldsByStatus(FieldStatus status, Pageable pageable) {
        log.debug("Fetching fields by status: {}", status);
        Page<FieldResponseDTO> page = fieldRepository.findByStatus(status, pageable)
                .map(fieldMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<FieldResponseDTO> searchFields(String query, Pageable pageable) {
        log.debug("Searching fields with query: {}", query);
        Page<FieldResponseDTO> page = fieldRepository.searchFields(query, pageable)
                .map(fieldMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional
    public FieldResponseDTO updateField(Long id, FieldRequestDTO requestDTO) {
        log.info("Updating field with ID: {}", id);
        Field existing = fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field", "id", id));

        Farm farm = null;
        if (requestDTO.getFarmId() != null) {
            farm = farmRepository.findById(requestDTO.getFarmId())
                    .orElseThrow(() -> new ResourceNotFoundException("Farm", "id", requestDTO.getFarmId()));
        }

        Crop crop = null;
        if (requestDTO.getCurrentCropId() != null) {
            crop = cropRepository.findById(requestDTO.getCurrentCropId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", requestDTO.getCurrentCropId()));
        }

        fieldMapper.updateEntityFromDTO(requestDTO, existing, farm, crop);
        Field updated = fieldRepository.save(existing);
        return fieldMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public FieldResponseDTO patchField(Long id, Map<String, Object> updates) {
        log.info("Applying partial updates to field ID: {}", id);
        Field existing = fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field", "id", id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> existing.setName((String) value);
                case "areaInAcres" -> existing.setAreaInAcres(Double.parseDouble(value.toString()));
                case "soilType" -> existing.setSoilType((String) value);
                case "irrigationType" -> existing.setIrrigationType((String) value);
                case "status" -> existing.setStatus(FieldStatus.valueOf(value.toString()));
                case "currentCropId" -> {
                    if (value == null) {
                        existing.setCurrentCrop(null);
                    } else {
                        Long cropId = Long.parseLong(value.toString());
                        Crop crop = cropRepository.findById(cropId)
                                .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", cropId));
                        existing.setCurrentCrop(crop);
                    }
                }
            }
        });

        Field saved = fieldRepository.save(existing);
        return fieldMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteField(Long id) {
        log.info("Deleting field with ID: {}", id);
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field", "id", id));
        fieldRepository.delete(field);
    }

    @Override
    @Transactional
    public FieldResponseDTO updateFieldStatus(Long id, FieldStatus status) {
        log.info("Updating status of field {} to {}", id, status);
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field", "id", id));
        field.setStatus(status);
        Field saved = fieldRepository.save(field);
        return fieldMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public FieldResponseDTO assignCropToField(Long fieldId, Long cropId) {
        log.info("Assigning crop {} to field {}", cropId, fieldId);
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Field", "id", fieldId));

        if (cropId == null) {
            field.setCurrentCrop(null);
            field.setStatus(FieldStatus.AVAILABLE);
        } else {
            Crop crop = cropRepository.findById(cropId)
                    .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", cropId));
            field.setCurrentCrop(crop);
            field.setStatus(FieldStatus.PLANTED);
        }

        Field saved = fieldRepository.save(field);
        return fieldMapper.toResponseDTO(saved);
    }
}
