package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.ExpenseRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ExpenseCategory;
import com.AgricultureFMSystem.AgricultureFMSystem.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "7. Expense Reports & Tracking", description = "Endpoints for recording expenses and generating financial aggregation reports")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @Operation(summary = "Log an expense", description = "Records a farming expenditure associated with a farm, field, or crop.")
    public ResponseEntity<ExpenseResponseDTO> createExpense(@Valid @RequestBody ExpenseRequestDTO requestDTO) {
        return new ResponseEntity<>(expenseService.createExpense(requestDTO), HttpStatus.CREATED);
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

        if (farmId != null || fieldId != null || category != null) {
            return ResponseEntity.ok(expenseService.getExpensesByFilters(farmId, fieldId, category, pageable));
        }
        return ResponseEntity.ok(expenseService.getAllExpenses(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense by ID", description = "Fetches expense details by ID.")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search expenses", description = "Search expenses by title, notes, or vendor name.")
    public ResponseEntity<PagedResponse<ExpenseResponseDTO>> searchExpenses(
            @RequestParam String query,
            @PageableDefault(page = 0, size = 10, sort = "expenseDate", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(expenseService.searchExpenses(query, pageable));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Filter expenses by date range", description = "Returns expenses logged between two dates.")
    public ResponseEntity<PagedResponse<ExpenseResponseDTO>> getExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(page = 0, size = 10, sort = "expenseDate", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(expenseService.getExpensesByDateRange(startDate, endDate, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Full update of an expense", description = "Replaces all details of an existing expense record.")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequestDTO requestDTO) {

        return ResponseEntity.ok(expenseService.updateExpense(id, requestDTO));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update of an expense", description = "Updates specific properties of an expense.")
    public ResponseEntity<ExpenseResponseDTO> patchExpense(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        return ResponseEntity.ok(expenseService.patchExpense(id, updates));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense", description = "Deletes an expense record.")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/report/by-category")
    @Operation(summary = "Expense report by category", description = "Business API: Aggregation query summing total expenses by category.")
    public ResponseEntity<List<ExpenseSummaryDTO>> getReportByCategory() {
        return ResponseEntity.ok(expenseService.getExpenseReportByCategory());
    }

    @GetMapping("/report/filtered")
    @Operation(summary = "Filtered expense report", description = "Business API: Aggregates category expenses filtered by farm and date boundaries.")
    public ResponseEntity<List<ExpenseSummaryDTO>> getFilteredExpenseReport(
            @RequestParam(required = false) Long farmId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(expenseService.getExpenseReportByFilters(farmId, startDate, endDate));
    }

    @GetMapping("/report/farm-total")
    @Operation(summary = "Total expenses for a farm", description = "Business API: Calculates grand total of all expenses for a given farm.")
    public ResponseEntity<BigDecimal> getTotalExpensesForFarm(@RequestParam Long farmId) {
        return ResponseEntity.ok(expenseService.getTotalExpensesByFarm(farmId));
    }
}
