package com.AgricultureFMSystem.AgricultureFMSystem.mapper;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.ExpenseRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.*;
import org.springframework.stereotype.Component;

/**
 * ============================================================================================
 * Phase 4: Entity <-> DTO Mapping Layer (ExpenseMapper)
 * ============================================================================================
 */
@Component
public class ExpenseMapper {

    public Expense toEntity(ExpenseRequestDTO dto, Farm farm, Field field, Crop crop, Activity activity) {
        if (dto == null) return null;

        return Expense.builder()
                .title(dto.getTitle())
                .category(dto.getCategory())
                .amount(dto.getAmount())
                .expenseDate(dto.getExpenseDate())
                .paymentMethod(dto.getPaymentMethod())
                .vendorName(dto.getVendorName())
                .notes(dto.getNotes())
                .farm(farm)
                .field(field)
                .crop(crop)
                .activity(activity)
                .build();
    }

    public ExpenseResponseDTO toResponseDTO(Expense entity) {
        if (entity == null) return null;

        return ExpenseResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .category(entity.getCategory())
                .amount(entity.getAmount())
                .expenseDate(entity.getExpenseDate())
                .paymentMethod(entity.getPaymentMethod())
                .vendorName(entity.getVendorName())
                .notes(entity.getNotes())
                .farmId(entity.getFarm() != null ? entity.getFarm().getId() : null)
                .farmName(entity.getFarm() != null ? entity.getFarm().getName() : null)
                .fieldId(entity.getField() != null ? entity.getField().getId() : null)
                .fieldName(entity.getField() != null ? entity.getField().getName() : null)
                .cropId(entity.getCrop() != null ? entity.getCrop().getId() : null)
                .cropName(entity.getCrop() != null ? entity.getCrop().getName() : null)
                .activityId(entity.getActivity() != null ? entity.getActivity().getId() : null)
                .activityTitle(entity.getActivity() != null ? entity.getActivity().getTitle() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(ExpenseRequestDTO dto, Expense entity, Farm farm, Field field, Crop crop, Activity activity) {
        if (dto == null || entity == null) return;

        entity.setTitle(dto.getTitle());
        entity.setCategory(dto.getCategory());
        entity.setAmount(dto.getAmount());
        entity.setExpenseDate(dto.getExpenseDate());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setVendorName(dto.getVendorName());
        entity.setNotes(dto.getNotes());
        if (farm != null) entity.setFarm(farm);
        entity.setField(field);
        entity.setCrop(crop);
        entity.setActivity(activity);
    }
}
