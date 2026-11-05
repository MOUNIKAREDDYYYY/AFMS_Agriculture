package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.ExpenseRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.ExpenseCategory;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 3: Service Layer - ExpenseService (Expense Reports & Aggregations)
 * ============================================================================================
 */
public interface ExpenseService {

    ExpenseResponseDTO createExpense(ExpenseRequestDTO requestDTO);

    ExpenseResponseDTO getExpenseById(Long id);

    PagedResponse<ExpenseResponseDTO> getAllExpenses(Pageable pageable);

    PagedResponse<ExpenseResponseDTO> getExpensesByFarmId(Long farmId, Pageable pageable);

    PagedResponse<ExpenseResponseDTO> getExpensesByFieldId(Long fieldId, Pageable pageable);

    PagedResponse<ExpenseResponseDTO> getExpensesByCategory(ExpenseCategory category, Pageable pageable);

    PagedResponse<ExpenseResponseDTO> getExpensesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    PagedResponse<ExpenseResponseDTO> searchExpenses(String query, Pageable pageable);

    ExpenseResponseDTO updateExpense(Long id, ExpenseRequestDTO requestDTO);

    ExpenseResponseDTO patchExpense(Long id, Map<String, Object> updates);

    void deleteExpense(Long id);

    // Business Reporting APIs
    List<ExpenseSummaryDTO> getExpenseReportByCategory();

    List<ExpenseSummaryDTO> getExpenseReportByFilters(Long farmId, LocalDate startDate, LocalDate endDate);

    BigDecimal getTotalExpensesByFarm(Long farmId);
}
