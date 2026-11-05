package com.AgricultureFMSystem.AgricultureFMSystem.mapper;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.CropRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.CropResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Crop;
import org.springframework.stereotype.Component;

/**
 * ============================================================================================
 * Phase 4: Entity <-> DTO Mapping Layer (CropMapper)
 * ============================================================================================
 */
@Component
public class CropMapper {

    public Crop toEntity(CropRequestDTO dto) {
        if (dto == null) return null;

        return Crop.builder()
                .name(dto.getName())
                .variety(dto.getVariety())
                .category(dto.getCategory())
                .season(dto.getSeason())
                .growthDurationDays(dto.getGrowthDurationDays())
                .optimalTemperature(dto.getOptimalTemperature())
                .waterRequirement(dto.getWaterRequirement())
                .standardYieldPerAcre(dto.getStandardYieldPerAcre())
                .yieldUnit(dto.getYieldUnit())
                .build();
    }

    public CropResponseDTO toResponseDTO(Crop entity) {
        if (entity == null) return null;

        return CropResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .variety(entity.getVariety())
                .category(entity.getCategory())
                .season(entity.getSeason())
                .growthDurationDays(entity.getGrowthDurationDays())
                .optimalTemperature(entity.getOptimalTemperature())
                .waterRequirement(entity.getWaterRequirement())
                .standardYieldPerAcre(entity.getStandardYieldPerAcre())
                .yieldUnit(entity.getYieldUnit())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(CropRequestDTO dto, Crop entity) {
        if (dto == null || entity == null) return;

        entity.setName(dto.getName());
        entity.setVariety(dto.getVariety());
        entity.setCategory(dto.getCategory());
        entity.setSeason(dto.getSeason());
        entity.setGrowthDurationDays(dto.getGrowthDurationDays());
        entity.setOptimalTemperature(dto.getOptimalTemperature());
        entity.setWaterRequirement(dto.getWaterRequirement());
        entity.setStandardYieldPerAcre(dto.getStandardYieldPerAcre());
        entity.setYieldUnit(dto.getYieldUnit());
    }
}
