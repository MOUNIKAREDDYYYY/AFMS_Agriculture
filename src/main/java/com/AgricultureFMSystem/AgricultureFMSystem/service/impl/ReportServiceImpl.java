package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmFinancialSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmerFinancialSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farm;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farmer;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Field;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.ActivityStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.ActivityMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.CropPlanMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.ExpenseMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.HarvestMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.*;
import com.AgricultureFMSystem.AgricultureFMSystem.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 3 & 4: Report Service Implementation (Financial & Field History Reporting)
 * ============================================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final FarmRepository farmRepository;
    private final FarmerRepository farmerRepository;
    private final FieldRepository fieldRepository;
    private final ExpenseRepository expenseRepository;
    private final HarvestRepository harvestRepository;
    private final ActivityRepository activityRepository;
    private final CropPlanRepository cropPlanRepository;

    private final ActivityMapper activityMapper;
    private final ExpenseMapper expenseMapper;
    private final HarvestMapper harvestMapper;
    private final CropPlanMapper cropPlanMapper;

    @Override
    public FarmFinancialSummaryDTO getFarmFinancialReport(Long farmId) {
        log.info("Generating comprehensive financial report for farm ID: {}", farmId);
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm", "id", farmId));

        BigDecimal totalExpenses = expenseRepository.sumExpensesByFarmId(farmId);
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        BigDecimal totalRevenue = harvestRepository.sumHarvestRevenueByFarmId(farmId);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        BigDecimal netProfitOrLoss = totalRevenue.subtract(totalExpenses);
        long completedActivities = activityRepository.countByFieldFarmIdAndStatus(farmId, ActivityStatus.COMPLETED);
        long harvestCount = harvestRepository.countHarvestsByFarmId(farmId);

        return FarmFinancialSummaryDTO.builder()
                .farmId(farm.getId())
                .farmName(farm.getName())
                .farmerId(farm.getFarmer() != null ? farm.getFarmer().getId() : null)
                .farmerName(farm.getFarmer() != null ? farm.getFarmer().getFullName() : null)
                .totalExpenses(totalExpenses)
                .totalHarvestRevenue(totalRevenue)
                .netProfitOrLoss(netProfitOrLoss)
                .totalActivitiesCompleted(completedActivities)
                .totalHarvestEvents(harvestCount)
                .build();
    }

    @Override
    public List<FarmFinancialSummaryDTO> getAllFarmsFinancialSummary() {
        log.info("Generating financial summary across all farms");
        List<Farm> allFarms = farmRepository.findAll();
        return allFarms.stream()
                .map(f -> getFarmFinancialReport(f.getId()))
                .toList();
    }

    @Override
    public FarmerFinancialSummaryDTO getFarmerFinancialReport(Long farmerId) {
        log.info("Generating aggregated financial statement for farmer ID: {}", farmerId);
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id", farmerId));

        List<Farm> farms = farmRepository.findByFarmerId(farmerId);
        BigDecimal totalExpenses = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        long totalActivities = 0;
        long totalHarvests = 0;

        for (Farm f : farms) {
            BigDecimal exp = expenseRepository.sumExpensesByFarmId(f.getId());
            if (exp != null) totalExpenses = totalExpenses.add(exp);

            BigDecimal rev = harvestRepository.sumHarvestRevenueByFarmId(f.getId());
            if (rev != null) totalRevenue = totalRevenue.add(rev);

            totalActivities += activityRepository.countByFieldFarmIdAndStatus(f.getId(), ActivityStatus.COMPLETED);
            totalHarvests += harvestRepository.countHarvestsByFarmId(f.getId());
        }

        BigDecimal netProfitOrLoss = totalRevenue.subtract(totalExpenses);

        return FarmerFinancialSummaryDTO.builder()
                .farmerId(farmer.getId())
                .farmerName(farmer.getFullName())
                .email(farmer.getEmail())
                .phoneNumber(farmer.getPhoneNumber())
                .totalFarms(farms.size())
                .totalExpenses(totalExpenses)
                .totalHarvestRevenue(totalRevenue)
                .netProfitOrLoss(netProfitOrLoss)
                .totalActivitiesCompleted(totalActivities)
                .totalHarvestEvents(totalHarvests)
                .build();
    }

    @Override
    public List<FarmerFinancialSummaryDTO> getAllFarmersFinancialSummary() {
        log.info("Generating financial summary across all farmers");
        List<Farmer> allFarmers = farmerRepository.findAll();
        return allFarmers.stream()
                .map(f -> getFarmerFinancialReport(f.getId()))
                .toList();
    }

    @Override
    public Map<String, Object> getFieldHistory(Long fieldId) {
        log.info("Generating lifecycle history for field ID: {}", fieldId);
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Field", "id", fieldId));

        Map<String, Object> history = new HashMap<>();
        history.put("fieldId", field.getId());
        history.put("fieldName", field.getName());
        history.put("farmName", field.getFarm() != null ? field.getFarm().getName() : null);
        history.put("status", field.getStatus());
        history.put("currentCrop", field.getCurrentCrop() != null ? field.getCurrentCrop().getName() : "None");

        // History of plans, activities, expenses and harvests
        history.put("cropPlans", field.getCropPlans().stream().map(cropPlanMapper::toResponseDTO).toList());
        history.put("activities", field.getActivities().stream().map(activityMapper::toResponseDTO).toList());
        history.put("expenses", field.getExpenses().stream().map(expenseMapper::toResponseDTO).toList());
        history.put("harvests", field.getHarvests().stream().map(harvestMapper::toResponseDTO).toList());

        return history;
    }
}
