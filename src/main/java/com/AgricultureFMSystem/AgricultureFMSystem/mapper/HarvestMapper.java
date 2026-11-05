package com.AgricultureFMSystem.AgricultureFMSystem.mapper;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.HarvestRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.HarvestResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Crop;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Field;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Harvest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * ============================================================================================
 * Phase 4: Entity <-> DTO Mapping Layer (HarvestMapper)
 * ============================================================================================
 */
@Component
public class HarvestMapper {

    public Harvest toEntity(HarvestRequestDTO dto, Field field, Crop crop) {
        if (dto == null) return null;

        BigDecimal calculatedRevenue = dto.getTotalRevenue();
        if (calculatedRevenue == null && dto.getUnitPrice() != null && dto.getQuantity() != null) {
            calculatedRevenue = dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
        }

        return Harvest.builder()
                .harvestDate(dto.getHarvestDate())
                .quantity(dto.getQuantity())
                .unit(dto.getUnit())
                .qualityGrade(dto.getQualityGrade())
                .unitPrice(dto.getUnitPrice())
                .totalRevenue(calculatedRevenue)
                .storageLocation(dto.getStorageLocation())
                .notes(dto.getNotes())
                .field(field)
                .crop(crop)
                .build();
    }

    public HarvestResponseDTO toResponseDTO(Harvest entity) {
        if (entity == null) return null;

        return HarvestResponseDTO.builder()
                .id(entity.getId())
                .harvestDate(entity.getHarvestDate())
                .quantity(entity.getQuantity())
                .unit(entity.getUnit())
                .qualityGrade(entity.getQualityGrade())
                .unitPrice(entity.getUnitPrice())
                .totalRevenue(entity.getTotalRevenue())
                .storageLocation(entity.getStorageLocation())
                .notes(entity.getNotes())
                .fieldId(entity.getField() != null ? entity.getField().getId() : null)
                .fieldName(entity.getField() != null ? entity.getField().getName() : null)
                .cropId(entity.getCrop() != null ? entity.getCrop().getId() : null)
                .cropName(entity.getCrop() != null ? entity.getCrop().getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(HarvestRequestDTO dto, Harvest entity, Field field, Crop crop) {
        if (dto == null || entity == null) return;

        entity.setHarvestDate(dto.getHarvestDate());
        entity.setQuantity(dto.getQuantity());
        entity.setUnit(dto.getUnit());
        entity.setQualityGrade(dto.getQualityGrade());
        entity.setUnitPrice(dto.getUnitPrice());

        BigDecimal calculatedRevenue = dto.getTotalRevenue();
        if (calculatedRevenue == null && dto.getUnitPrice() != null && dto.getQuantity() != null) {
            calculatedRevenue = dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
        }
        entity.setTotalRevenue(calculatedRevenue);

        entity.setStorageLocation(dto.getStorageLocation());
        entity.setNotes(dto.getNotes());
        if (field != null) entity.setField(field);
        if (crop != null) entity.setCrop(crop);
    }
}
