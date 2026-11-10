package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import com.AgricultureFMSystem.AgricultureFMSystem.enums.FieldStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Strongly typed lifecycle history response for a single Field plot.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldHistoryResponseDTO {

    private Long fieldId;
    private String fieldName;
    private String farmName;
    private FieldStatus status;
    private String currentCrop;
    private List<CropPlanResponseDTO> cropPlans;
    private List<ActivityResponseDTO> activities;
    private List<ExpenseResponseDTO> expenses;
    private List<HarvestResponseDTO> harvests;
}

