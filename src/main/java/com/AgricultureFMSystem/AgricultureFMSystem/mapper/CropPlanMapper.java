package com.AgricultureFMSystem.AgricultureFMSystem.mapper;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.CropPlanRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.CropPlanResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Crop;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.CropPlan;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Field;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.CropPlanStatus;
import org.springframework.stereotype.Component;

/**
 * ============================================================================================
 * Phase 4: Entity <-> DTO Mapping Layer (CropPlanMapper)
 * ============================================================================================
 */
@Component
public class CropPlanMapper {

    public CropPlan toEntity(CropPlanRequestDTO dto, Field field, Crop crop) {
        if (dto == null) return null;

        return CropPlan.builder()
                .field(field)
                .crop(crop)
                .season(dto.getSeason())
                .plannedStartDate(dto.getPlannedStartDate())
                .estimatedHarvestDate(dto.getEstimatedHarvestDate())
                .expectedYieldQuantity(dto.getExpectedYieldQuantity())
                .yieldUnit(dto.getYieldUnit())
                .estimatedBudget(dto.getEstimatedBudget())
                .status(dto.getStatus() != null ? dto.getStatus() : CropPlanStatus.DRAFT)
                .notes(dto.getNotes())
                .build();
    }

    public CropPlanResponseDTO toResponseDTO(CropPlan entity) {
        if (entity == null) return null;

        return CropPlanResponseDTO.builder()
                .id(entity.getId())
                .fieldId(entity.getField() != null ? entity.getField().getId() : null)
                .fieldName(entity.getField() != null ? entity.getField().getName() : null)
                .cropId(entity.getCrop() != null ? entity.getCrop().getId() : null)
                .cropName(entity.getCrop() != null ? entity.getCrop().getName() : null)
                .season(entity.getSeason())
                .plannedStartDate(entity.getPlannedStartDate())
                .estimatedHarvestDate(entity.getEstimatedHarvestDate())
                .expectedYieldQuantity(entity.getExpectedYieldQuantity())
                .yieldUnit(entity.getYieldUnit())
                .estimatedBudget(entity.getEstimatedBudget())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(CropPlanRequestDTO dto, CropPlan entity, Field field, Crop crop) {
        if (dto == null || entity == null) return;

        if (field != null) entity.setField(field);
        if (crop != null) entity.setCrop(crop);
        entity.setSeason(dto.getSeason());
        entity.setPlannedStartDate(dto.getPlannedStartDate());
        entity.setEstimatedHarvestDate(dto.getEstimatedHarvestDate());
        entity.setExpectedYieldQuantity(dto.getExpectedYieldQuantity());
        entity.setYieldUnit(dto.getYieldUnit());
        entity.setEstimatedBudget(dto.getEstimatedBudget());
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        entity.setNotes(dto.getNotes());
    }
}
