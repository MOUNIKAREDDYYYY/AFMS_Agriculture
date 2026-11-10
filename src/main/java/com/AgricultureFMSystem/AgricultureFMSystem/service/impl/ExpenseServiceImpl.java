package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.ExpenseRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.*;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ExpenseCategory;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.ExpenseMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.*;
import com.AgricultureFMSystem.AgricultureFMSystem.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 3: Expense Service Implementation with Aggregation Queries
 * ============================================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final FarmRepository farmRepository;
    private final FieldRepository fieldRepository;
    private final CropRepository cropRepository;
    private final ActivityRepository activityRepository;
    private final ExpenseMapper expenseMapper;

    @Override
    @Transactional
    @CacheEvict(value = "fieldHistory", allEntries = true)
    public ExpenseResponseDTO createExpense(ExpenseRequestDTO requestDTO) {
        log.info("Recording expense '{}' of amount {} for farm ID {}",
                requestDTO.getTitle(), requestDTO.getAmount(), requestDTO.getFarmId());

        Farm farm = farmRepository.findById(requestDTO.getFarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm", "id", requestDTO.getFarmId()));

        Field field = null;
        if (requestDTO.getFieldId() != null) {
            field = fieldRepository.findById(requestDTO.getFieldId())
                    .orElseThrow(() -> new ResourceNotFoundException("Field", "id", requestDTO.getFieldId()));
        }

        Crop crop = null;
        if (requestDTO.getCropId() != null) {
            crop = cropRepository.findById(requestDTO.getCropId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", requestDTO.getCropId()));
        }

        Activity activity = null;
        if (requestDTO.getActivityId() != null) {
            activity = activityRepository.findById(requestDTO.getActivityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", requestDTO.getActivityId()));
        }

        Expense expense = expenseMapper.toEntity(requestDTO, farm, field, crop, activity);
        Expense saved = expenseRepository.save(expense);
        return expenseMapper.toResponseDTO(saved);
    }

    @Override
    public ExpenseResponseDTO getExpenseById(Long id) {
        log.debug("Fetching expense ID: {}", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        return expenseMapper.toResponseDTO(expense);
    }

    @Override
    public PagedResponse<ExpenseResponseDTO> getAllExpenses(Pageable pageable) {
        log.debug("Fetching all expenses with pagination");
        Page<ExpenseResponseDTO> page = expenseRepository.findAll(pageable)
                .map(expenseMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<ExpenseResponseDTO> getExpensesByFilters(Long farmId, Long fieldId, ExpenseCategory category, Pageable pageable) {
        log.debug("Fetching expenses with filters: farmId={}, fieldId={}, category={}", farmId, fieldId, category);
        if (farmId != null && !farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Farm", "id", farmId);
        }
        if (fieldId != null && !fieldRepository.existsById(fieldId)) {
            throw new ResourceNotFoundException("Field", "id", fieldId);
        }
        Page<ExpenseResponseDTO> page = expenseRepository.findByFilters(farmId, fieldId, category, pageable)
                .map(expenseMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<ExpenseResponseDTO> getExpensesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.debug("Fetching expenses between {} and {}", startDate, endDate);
        Page<ExpenseResponseDTO> page = expenseRepository.findByExpenseDateBetween(startDate, endDate, pageable)
                .map(expenseMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<ExpenseResponseDTO> searchExpenses(String query, Pageable pageable) {
        log.debug("Searching expenses with query: {}", query);
        Page<ExpenseResponseDTO> page = expenseRepository.searchExpenses(query, pageable)
                .map(expenseMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional
    @CacheEvict(value = "fieldHistory", allEntries = true)
    public ExpenseResponseDTO updateExpense(Long id, ExpenseRequestDTO requestDTO) {
        log.info("Updating expense ID: {}", id);
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));

        Farm farm = null;
        if (requestDTO.getFarmId() != null) {
            farm = farmRepository.findById(requestDTO.getFarmId())
                    .orElseThrow(() -> new ResourceNotFoundException("Farm", "id", requestDTO.getFarmId()));
        }

        Field field = null;
        if (requestDTO.getFieldId() != null) {
            field = fieldRepository.findById(requestDTO.getFieldId())
                    .orElseThrow(() -> new ResourceNotFoundException("Field", "id", requestDTO.getFieldId()));
        }

        Crop crop = null;
        if (requestDTO.getCropId() != null) {
            crop = cropRepository.findById(requestDTO.getCropId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", requestDTO.getCropId()));
        }

        Activity activity = null;
        if (requestDTO.getActivityId() != null) {
            activity = activityRepository.findById(requestDTO.getActivityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", requestDTO.getActivityId()));
        }

        expenseMapper.updateEntityFromDTO(requestDTO, existing, farm, field, crop, activity);
        Expense saved = expenseRepository.save(existing);
        return expenseMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "fieldHistory", allEntries = true)
    public ExpenseResponseDTO patchExpense(Long id, Map<String, Object> updates) {
        log.info("Applying partial updates to expense ID: {}", id);
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "title" -> existing.setTitle(PatchHelper.requireNonBlankString("title", value));
                case "category" -> existing.setCategory(PatchHelper.requireEnum("category", value, ExpenseCategory.class));
                case "amount" -> existing.setAmount(PatchHelper.requireBigDecimal("amount", value, true));
                case "expenseDate" -> existing.setExpenseDate(PatchHelper.requireLocalDate("expenseDate", value));
                case "paymentMethod" -> existing.setPaymentMethod(PatchHelper.optionalString("paymentMethod", value));
                case "vendorName" -> existing.setVendorName(PatchHelper.optionalString("vendorName", value));
                case "notes" -> existing.setNotes(PatchHelper.optionalString("notes", value));
            }
        });

        Expense saved = expenseRepository.save(existing);
        return expenseMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "fieldHistory", allEntries = true)
    public void deleteExpense(Long id) {
        log.info("Deleting expense ID: {}", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        expenseRepository.delete(expense);
    }

    @Override
    public List<ExpenseSummaryDTO> getExpenseReportByCategory() {
        log.info("Generating expense summary report by category");
        return expenseRepository.aggregateExpensesByCategory();
    }

    @Override
    public List<ExpenseSummaryDTO> getExpenseReportByFilters(Long farmId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating filtered expense report for farm ID {} from {} to {}", farmId, startDate, endDate);
        return expenseRepository.aggregateExpensesByCategoryAndFilters(farmId, startDate, endDate);
    }

    @Override
    public BigDecimal getTotalExpensesByFarm(Long farmId) {
        log.debug("Calculating total expenses for farm ID: {}", farmId);
        return expenseRepository.sumExpensesByFarmId(farmId);
    }
}
