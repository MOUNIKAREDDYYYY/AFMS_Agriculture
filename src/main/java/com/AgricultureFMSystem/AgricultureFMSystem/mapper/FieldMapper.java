package com.AgricultureFMSystem.AgricultureFMSystem.mapper;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FieldRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FieldResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Crop;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farm;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Field;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.FieldStatus;
import org.springframework.stereotype.Component;

/**
 * ============================================================================================
 * Phase 4: Entity <-> DTO Mapping Layer (FieldMapper)
 * ============================================================================================
 */
@Component
public class FieldMapper {

    public Field toEntity(FieldRequestDTO dto, Farm farm, Crop crop) {
        if (dto == null) return null;

        return Field.builder()
                .name(dto.getName())
                .areaInAcres(dto.getAreaInAcres())
                .soilType(dto.getSoilType())
                .irrigationType(dto.getIrrigationType())
                .status(dto.getStatus() != null ? dto.getStatus() : FieldStatus.AVAILABLE)
                .farm(farm)
                .currentCrop(crop)
                .build();
    }

    public FieldResponseDTO toResponseDTO(Field entity) {
        if (entity == null) return null;

        return FieldResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .areaInAcres(entity.getAreaInAcres())
                .soilType(entity.getSoilType())
                .irrigationType(entity.getIrrigationType())
                .status(entity.getStatus())
                .farmId(entity.getFarm() != null ? entity.getFarm().getId() : null)
                .farmName(entity.getFarm() != null ? entity.getFarm().getName() : null)
                .currentCropId(entity.getCurrentCrop() != null ? entity.getCurrentCrop().getId() : null)
                .currentCropName(entity.getCurrentCrop() != null ? entity.getCurrentCrop().getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(FieldRequestDTO dto, Field entity, Farm farm, Crop crop) {
        if (dto == null || entity == null) return;

        entity.setName(dto.getName());
        entity.setAreaInAcres(dto.getAreaInAcres());
        entity.setSoilType(dto.getSoilType());
        entity.setIrrigationType(dto.getIrrigationType());
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (farm != null) {
            entity.setFarm(farm);
        }
        entity.setCurrentCrop(crop);
    }
}
