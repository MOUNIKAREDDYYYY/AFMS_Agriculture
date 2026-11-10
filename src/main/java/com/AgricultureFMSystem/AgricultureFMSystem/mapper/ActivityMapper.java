package com.AgricultureFMSystem.AgricultureFMSystem.mapper;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.ActivityRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ActivityResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Activity;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Crop;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Field;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityStatus;
import org.springframework.stereotype.Component;

/**
 * ============================================================================================
 * Phase 4: Entity <-> DTO Mapping Layer (ActivityMapper)
 * ============================================================================================
 */
@Component
public class ActivityMapper {

    public Activity toEntity(ActivityRequestDTO dto, Field field, Crop crop) {
        if (dto == null) return null;

        return Activity.builder()
                .title(dto.getTitle())
                .activityType(dto.getActivityType())
                .status(dto.getStatus() != null ? dto.getStatus() : ActivityStatus.PLANNED)
                .scheduledDate(dto.getScheduledDate())
                .completionDate(dto.getCompletionDate())
                .assignedTo(dto.getAssignedTo())
                .notes(dto.getNotes())
                .field(field)
                .crop(crop)
                .build();
    }

    public ActivityResponseDTO toResponseDTO(Activity entity) {
        if (entity == null) return null;

        return ActivityResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .activityType(entity.getActivityType())
                .status(entity.getStatus())
                .scheduledDate(entity.getScheduledDate())
                .completionDate(entity.getCompletionDate())
                .assignedTo(entity.getAssignedTo())
                .notes(entity.getNotes())
                .fieldId(entity.getField() != null ? entity.getField().getId() : null)
                .fieldName(entity.getField() != null ? entity.getField().getName() : null)
                .cropId(entity.getCrop() != null ? entity.getCrop().getId() : null)
                .cropName(entity.getCrop() != null ? entity.getCrop().getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(ActivityRequestDTO dto, Activity entity, Field field, Crop crop) {
        if (dto == null || entity == null) return;

        entity.setTitle(dto.getTitle());
        entity.setActivityType(dto.getActivityType());
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        entity.setScheduledDate(dto.getScheduledDate());
        entity.setCompletionDate(dto.getCompletionDate());
        entity.setAssignedTo(dto.getAssignedTo());
        entity.setNotes(dto.getNotes());
        if (field != null) entity.setField(field);
        entity.setCrop(crop);
    }
}
