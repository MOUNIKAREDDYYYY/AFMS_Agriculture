package com.AgricultureFMSystem.AgricultureFMSystem.repository;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Expense;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * ============================================================================================
 * Phase 3: Spring Data JPA Repository - ExpenseRepository (Expense Reports & Aggregations)
 * ============================================================================================
 * Demonstrates:
 * - Aggregation Queries (SUM, COUNT, GROUP BY) with Constructor Expression into DTOs
 * - Date Range Filtering (BETWEEN :startDate AND :endDate)
 * - Multi-criteria search
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByFarmId(Long farmId);

    @Query("SELECT e FROM Expense e WHERE " +
           "(:farmId IS NULL OR e.farm.id = :farmId) AND " +
           "(:fieldId IS NULL OR e.field.id = :fieldId) AND " +
           "(:category IS NULL OR e.category = :category)")
    Page<Expense> findByFilters(@Param("farmId") Long farmId,
                                @Param("fieldId") Long fieldId,
                                @Param("category") ExpenseCategory category,
                                Pageable pageable);

    // Date Filtering: Find expenses incurred between two dates
    @Query("SELECT e FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate ORDER BY e.expenseDate DESC")
    Page<Expense> findByExpenseDateBetween(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           Pageable pageable);

    // Aggregation Query 1: Total expense amount for a farm
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.farm.id = :farmId")
    BigDecimal sumExpensesByFarmId(@Param("farmId") Long farmId);

    // Batch aggregation: Total expense amount grouped by farm ID
    @Query("SELECT e.farm.id, COALESCE(SUM(e.amount), 0) FROM Expense e GROUP BY e.farm.id")
    List<Object[]> sumExpensesGroupedByFarm();

    // Aggregation Query 2: Total expense sum across date range
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal sumExpensesByDateRange(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    // Aggregation Query 3: Group by Category across all expenses
    @Query("SELECT new com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseSummaryDTO(" +
           "e.category, SUM(e.amount), COUNT(e.id)) " +
           "FROM Expense e " +
           "GROUP BY e.category " +
           "ORDER BY SUM(e.amount) DESC")
    List<ExpenseSummaryDTO> aggregateExpensesByCategory();

    // Aggregation Query 4: Group by Category for a specific Farm and Date Range
    @Query("SELECT new com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseSummaryDTO(" +
           "e.category, SUM(e.amount), COUNT(e.id)) " +
           "FROM Expense e " +
           "WHERE (:farmId IS NULL OR e.farm.id = :farmId) " +
           "AND (:startDate IS NULL OR e.expenseDate >= :startDate) " +
           "AND (:endDate IS NULL OR e.expenseDate <= :endDate) " +
           "GROUP BY e.category " +
           "ORDER BY SUM(e.amount) DESC")
    List<ExpenseSummaryDTO> aggregateExpensesByCategoryAndFilters(
            @Param("farmId") Long farmId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Search expenses
    @Query("SELECT e FROM Expense e WHERE " +
           "LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.notes) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.vendorName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Expense> searchExpenses(@Param("query") String query, Pageable pageable);
}
