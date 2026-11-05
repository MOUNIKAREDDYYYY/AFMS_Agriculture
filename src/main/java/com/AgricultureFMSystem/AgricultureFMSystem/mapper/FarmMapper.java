package com.AgricultureFMSystem.AgricultureFMSystem.mapper;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FarmRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farm;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farmer;
import org.springframework.stereotype.Component;

/**
 * ============================================================================================
 * Phase 4: Entity <-> DTO Mapping Layer (FarmMapper)
 * ============================================================================================
 */
@Component
public class FarmMapper {

    public Farm toEntity(FarmRequestDTO dto, Farmer farmer) {
        if (dto == null) return null;

        return Farm.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .totalAreaInAcres(dto.getTotalAreaInAcres())
                .farmer(farmer)
                .build();
    }

    public FarmResponseDTO toResponseDTO(Farm entity) {
        if (entity == null) return null;

        return FarmResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .location(entity.getLocation())
                .totalAreaInAcres(entity.getTotalAreaInAcres())
                .farmerId(entity.getFarmer() != null ? entity.getFarmer().getId() : null)
                .farmerName(entity.getFarmer() != null ? entity.getFarmer().getFullName() : null)
                .totalFields(entity.getFields() != null ? entity.getFields().size() : 0)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(FarmRequestDTO dto, Farm entity, Farmer farmer) {
        if (dto == null || entity == null) return;

        entity.setName(dto.getName());
        entity.setLocation(dto.getLocation());
        entity.setTotalAreaInAcres(dto.getTotalAreaInAcres());
        if (farmer != null) {
            entity.setFarmer(farmer);
        }
    }
}
