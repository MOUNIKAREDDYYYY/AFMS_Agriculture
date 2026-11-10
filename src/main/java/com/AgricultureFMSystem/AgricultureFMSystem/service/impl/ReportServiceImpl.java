package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmFinancialSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmerFinancialSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FieldHistoryResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farm;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farmer;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Field;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ActivityStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.ActivityMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.CropPlanMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.ExpenseMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.HarvestMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.*;
import com.AgricultureFMSystem.AgricultureFMSystem.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates single-entity and batch-aggregated financial and field history reports.
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

    private final ActivityMapper activityMapper;
    private final ExpenseMapper expenseMapper;
    private final HarvestMapper harvestMapper;
    private final CropPlanMapper cropPlanMapper;

    @Override
    public FarmFinancialSummaryDTO getFarmFinancialReport(Long farmId) {
        log.info("Generating comprehensive financial report for farm ID: {}", farmId);
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm", "id", farmId));

        BigDecimal totalExpenses = nonNull(expenseRepository.sumExpensesByFarmId(farmId));
        BigDecimal totalRevenue = nonNull(harvestRepository.sumHarvestRevenueByFarmId(farmId));
        long completedActivities = activityRepository.countByFieldFarmIdAndStatus(farmId, ActivityStatus.COMPLETED);
        long harvestCount = harvestRepository.countHarvestsByFarmId(farmId);

        return buildFarmSummary(farm, totalExpenses, totalRevenue, completedActivities, harvestCount);
    }

    @Override
    public List<FarmFinancialSummaryDTO> getAllFarmsFinancialSummary() {
        log.info("Generating financial summary across all farms (batch aggregated)");
        List<Farm> allFarms = farmRepository.findAllWithFarmer();
        Map<Long, BigDecimal> expensesByFarm = toBigDecimalMap(expenseRepository.sumExpensesGroupedByFarm());
        Map<Long, BigDecimal> revenueByFarm = toBigDecimalMap(harvestRepository.sumRevenueGroupedByFarm());
        Map<Long, Long> harvestsByFarm = toCountMap(harvestRepository.countHarvestsGroupedByFarm());
        Map<Long, Long> completedByFarm = toCountMap(
                activityRepository.countByStatusGroupedByFarm(ActivityStatus.COMPLETED));

        return allFarms.stream()
                .map(farm -> buildFarmSummary(
                        farm,
                        expensesByFarm.getOrDefault(farm.getId(), BigDecimal.ZERO),
                        revenueByFarm.getOrDefault(farm.getId(), BigDecimal.ZERO),
                        completedByFarm.getOrDefault(farm.getId(), 0L),
                        harvestsByFarm.getOrDefault(farm.getId(), 0L)))
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
            totalExpenses = totalExpenses.add(nonNull(expenseRepository.sumExpensesByFarmId(f.getId())));
            totalRevenue = totalRevenue.add(nonNull(harvestRepository.sumHarvestRevenueByFarmId(f.getId())));
            totalActivities += activityRepository.countByFieldFarmIdAndStatus(f.getId(), ActivityStatus.COMPLETED);
            totalHarvests += harvestRepository.countHarvestsByFarmId(f.getId());
        }

        return buildFarmerSummary(farmer, farms.size(), totalExpenses, totalRevenue, totalActivities, totalHarvests);
    }

    @Override
    public List<FarmerFinancialSummaryDTO> getAllFarmersFinancialSummary() {
        log.info("Generating financial summary across all farmers (batch aggregated)");
        List<Farmer> allFarmers = farmerRepository.findAll();
        Map<Long, List<Farm>> farmsByFarmer = farmRepository.findAllWithFarmer().stream()
                .filter(f -> f.getFarmer() != null)
                .collect(Collectors.groupingBy(f -> f.getFarmer().getId()));
        Map<Long, BigDecimal> expensesByFarm = toBigDecimalMap(expenseRepository.sumExpensesGroupedByFarm());
        Map<Long, BigDecimal> revenueByFarm = toBigDecimalMap(harvestRepository.sumRevenueGroupedByFarm());
        Map<Long, Long> harvestsByFarm = toCountMap(harvestRepository.countHarvestsGroupedByFarm());
        Map<Long, Long> completedByFarm = toCountMap(
                activityRepository.countByStatusGroupedByFarm(ActivityStatus.COMPLETED));

        return allFarmers.stream()
                .map(farmer -> {
                    List<Farm> farms = farmsByFarmer.getOrDefault(farmer.getId(), List.of());
                    BigDecimal totalExpenses = BigDecimal.ZERO;
                    BigDecimal totalRevenue = BigDecimal.ZERO;
                    long totalActivities = 0;
                    long totalHarvests = 0;
                    for (Farm f : farms) {
                        totalExpenses = totalExpenses.add(expensesByFarm.getOrDefault(f.getId(), BigDecimal.ZERO));
                        totalRevenue = totalRevenue.add(revenueByFarm.getOrDefault(f.getId(), BigDecimal.ZERO));
                        totalActivities += completedByFarm.getOrDefault(f.getId(), 0L);
                        totalHarvests += harvestsByFarm.getOrDefault(f.getId(), 0L);
                    }
                    return buildFarmerSummary(farmer, farms.size(), totalExpenses, totalRevenue, totalActivities, totalHarvests);
                })
                .toList();
    }

    @Override
    @Cacheable(value = "fieldHistory", key = "#fieldId")
    public FieldHistoryResponseDTO getFieldHistory(Long fieldId) {
        log.info("Generating lifecycle history for field ID: {}", fieldId);
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Field", "id", fieldId));

        return FieldHistoryResponseDTO.builder()
                .fieldId(field.getId())
                .fieldName(field.getName())
                .farmName(field.getFarm() != null ? field.getFarm().getName() : null)
                .status(field.getStatus())
                .currentCrop(field.getCurrentCrop() != null ? field.getCurrentCrop().getName() : "None")
                .cropPlans(field.getCropPlans().stream().map(cropPlanMapper::toResponseDTO).toList())
                .activities(field.getActivities().stream().map(activityMapper::toResponseDTO).toList())
                .expenses(field.getExpenses().stream().map(expenseMapper::toResponseDTO).toList())
                .harvests(field.getHarvests().stream().map(harvestMapper::toResponseDTO).toList())
                .build();
    }

    private FarmFinancialSummaryDTO buildFarmSummary(
            Farm farm, BigDecimal totalExpenses, BigDecimal totalRevenue,
            long completedActivities, long harvestCount) {
        return FarmFinancialSummaryDTO.builder()
                .farmId(farm.getId())
                .farmName(farm.getName())
                .farmerId(farm.getFarmer() != null ? farm.getFarmer().getId() : null)
                .farmerName(farm.getFarmer() != null ? farm.getFarmer().getFullName() : null)
                .totalExpenses(totalExpenses)
                .totalHarvestRevenue(totalRevenue)
                .netProfitOrLoss(totalRevenue.subtract(totalExpenses))
                .totalActivitiesCompleted(completedActivities)
                .totalHarvestEvents(harvestCount)
                .build();
    }

    private FarmerFinancialSummaryDTO buildFarmerSummary(
            Farmer farmer, int totalFarms, BigDecimal totalExpenses,
            BigDecimal totalRevenue, long totalActivities, long totalHarvests) {
        return FarmerFinancialSummaryDTO.builder()
                .farmerId(farmer.getId())
                .farmerName(farmer.getFullName())
                .email(farmer.getEmail())
                .phoneNumber(farmer.getPhoneNumber())
                .totalFarms(totalFarms)
                .totalExpenses(totalExpenses)
                .totalHarvestRevenue(totalRevenue)
                .netProfitOrLoss(totalRevenue.subtract(totalExpenses))
                .totalActivitiesCompleted(totalActivities)
                .totalHarvestEvents(totalHarvests)
                .build();
    }

    private static BigDecimal nonNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private static Map<Long, BigDecimal> toBigDecimalMap(List<Object[]> rows) {
        Map<Long, BigDecimal> map = new HashMap<>();
        for (Object[] row : rows) {
            if (row[0] != null) {
                map.put(((Number) row[0]).longValue(), nonNull((BigDecimal) row[1]));
            }
        }
        return map;
    }

    private static Map<Long, Long> toCountMap(List<Object[]> rows) {
        Map<Long, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            if (row[0] != null && row[1] != null) {
                map.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
            }
        }
        return map;
    }
}
