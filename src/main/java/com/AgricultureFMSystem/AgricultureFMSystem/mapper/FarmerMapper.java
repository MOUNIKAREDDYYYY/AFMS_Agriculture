package com.AgricultureFMSystem.AgricultureFMSystem.mapper;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FarmerRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmerResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farmer;
import org.springframework.stereotype.Component;

/**
 * ============================================================================================
 * Phase 4: Entity <-> DTO Mapping Layer (FarmerMapper)
 * ============================================================================================
 * Encapsulates entity-to-DTO and DTO-to-entity conversions.
 */
@Component
public class FarmerMapper {

    public Farmer toEntity(FarmerRequestDTO dto) {
        if (dto == null) return null;

        return Farmer.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .governmentIdNumber(dto.getGovernmentIdNumber())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public FarmerResponseDTO toResponseDTO(Farmer entity) {
        if (entity == null) return null;

        return FarmerResponseDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .governmentIdNumber(entity.getGovernmentIdNumber())
                .active(entity.getActive())
                .totalFarms(entity.getFarms() != null ? entity.getFarms().size() : 0)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(FarmerRequestDTO dto, Farmer entity) {
        if (dto == null || entity == null) return;

        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setAddress(dto.getAddress());
        entity.setGovernmentIdNumber(dto.getGovernmentIdNumber());
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
