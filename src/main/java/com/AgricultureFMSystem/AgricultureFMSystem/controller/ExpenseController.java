package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.ExpenseRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.ExpenseCategory;
import com.AgricultureFMSystem.AgricultureFMSystem.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2, 3 & 4: Expense REST Controller (Expense Reports & Aggregations)
 * ============================================================================================
 * Endpoints:
 * - POST   /api/expenses                      -> Log expense
 * - GET    /api/expenses                      -> List / filter by farm, field, category
 * - GET    /api/expenses/{id}                 -> Get by ID
 * - PUT    /api/expenses/{id}                 -> Full update
 * - PATCH  /api/expenses/{id}                 -> Partial update
 * - DELETE /api/expenses/{id}                 -> Delete expense
 * - GET    /api/expenses/search               -> Search expenses
 * - GET    /api/expenses/date-range           -> Filter expenses by date interval
 * - GET    /api/expenses/report/by-category   -> Aggregation: sum/count by category
 * - GET    /api/expenses/report/filtered      -> Aggregation: sum/count by category with farm & date filters
 * - GET    /api/expenses/report/farm-total    -> Total expenditure sum for a farm
 */
@Slf4j
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "7. Expense Reports & Tracking", description = "Endpoints for recording expenses and generating financial aggregation reports")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @Operation(summary = "Log an expense", description = "Records a farming expenditure associated with a farm, field, or crop.")
    public ResponseEntity<ExpenseResponseDTO> createExpense(@Valid @RequestBody ExpenseRequestDTO requestDTO) {
        log.info("REST request to record expense: {}", requestDTO.getTitle());
        ExpenseResponseDTO response = expenseService.createExpense(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List expenses", description = "Returns paginated expenses with optional farm, field, and category filters.")
    public ResponseEntity<PagedResponse<ExpenseResponseDTO>> getAllExpenses(
            @Parameter(description = "Filter by Farm ID")
            @RequestParam(required = false) Long farmId,
            @Parameter(description = "Filter by Field ID")
            @RequestParam(required = false) Long fieldId,
            @Parameter(description = "Filter by Expense Category (SEEDS, FERTILIZERS, LABOR, etc.)")
            @RequestParam(required = false) ExpenseCategory category,
            @PageableDefault(page = 0, size = 10, sort = "expenseDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("REST request to list expenses: farmId={}, fieldId={}, category={}", farmId, fieldId, category);
        if (farmId != null) {
            return ResponseEntity.ok(expenseService.getExpensesByFarmId(farmId, pageable));
        }
        if (fieldId != null) {
            return ResponseEntity.ok(expenseService.getExpensesByFieldId(fieldId, pageable));
        }
        if (category != null) {
            return ResponseEntity.ok(expenseService.getExpensesByCategory(category, pageable));
        }
        return ResponseEntity.ok(expenseService.getAllExpenses(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense by ID", description = "Fetches expense details by ID.")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Long id) {
        log.debug("REST request to get expense ID: {}", id);
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search expenses", description = "Search expenses by title, notes, or vendor name.")
    public ResponseEntity<PagedResponse<ExpenseResponseDTO>> searchExpenses(
            @RequestParam String query,
            @PageableDefault(page = 0, size = 10, sort = "expenseDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("REST search expenses with query: {}", query);
        return ResponseEntity.ok(expenseService.searchExpenses(query, pageable));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Filter expenses by date range", description = "Returns expenses logged between two dates.")
    public ResponseEntity<PagedResponse<ExpenseResponseDTO>> getExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(page = 0, size = 10, sort = "expenseDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("REST request for expenses between {} and {}", startDate, endDate);
        return ResponseEntity.ok(expenseService.getExpensesByDateRange(startDate, endDate, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Full update of an expense", description = "Replaces all details of an existing expense record.")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequestDTO requestDTO) {

        log.info("REST request to update expense ID: {}", id);
        return ResponseEntity.ok(expenseService.updateExpense(id, requestDTO));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update of an expense", description = "Updates specific properties of an expense.")
    public ResponseEntity<ExpenseResponseDTO> patchExpense(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        log.info("REST request to patch expense ID: {}", id);
        return ResponseEntity.ok(expenseService.patchExpense(id, updates));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense", description = "Deletes an expense record.")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        log.info("REST request to delete expense ID: {}", id);
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/report/by-category")
    @Operation(summary = "Expense report by category", description = "Business API: Aggregation query summing total expenses by category.")
    public ResponseEntity<List<ExpenseSummaryDTO>> getReportByCategory() {
        log.info("REST request for expense summary report grouped by category");
        return ResponseEntity.ok(expenseService.getExpenseReportByCategory());
    }

    @GetMapping("/report/filtered")
    @Operation(summary = "Filtered expense report", description = "Business API: Aggregates category expenses filtered by farm and date boundaries.")
    public ResponseEntity<List<ExpenseSummaryDTO>> getFilteredExpenseReport(
            @RequestParam(required = false) Long farmId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("REST request for filtered expense report: farmId={}, start={}, end={}", farmId, startDate, endDate);
        return ResponseEntity.ok(expenseService.getExpenseReportByFilters(farmId, startDate, endDate));
    }

    @GetMapping("/report/farm-total")
    @Operation(summary = "Total expenses for a farm", description = "Business API: Calculates grand total of all expenses for a given farm.")
    public ResponseEntity<BigDecimal> getTotalExpensesForFarm(@RequestParam Long farmId) {
        log.info("REST request for total farm expenses: farmId={}", farmId);
        return ResponseEntity.ok(expenseService.getTotalExpensesByFarm(farmId));
    }
}
