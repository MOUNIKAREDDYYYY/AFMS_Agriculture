package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ExpenseSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.ExpenseCategory;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.ExpenseRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ============================================================================================
 * Phase 5: Testing with JUnit 5 & Mockito - ExpenseServiceTest (Aggregation Reporting Tests)
 * ============================================================================================
 */
@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Test
    @DisplayName("Should return aggregate expense summaries grouped by category")
    void getExpenseReportByCategory_Success() {
        // Arrange
        ExpenseSummaryDTO summary1 = new ExpenseSummaryDTO(ExpenseCategory.SEEDS, new BigDecimal("45000.00"), 3L);
        ExpenseSummaryDTO summary2 = new ExpenseSummaryDTO(ExpenseCategory.FERTILIZERS, new BigDecimal("65000.00"), 5L);

        when(expenseRepository.aggregateExpensesByCategory()).thenReturn(List.of(summary1, summary2));

        // Act
        List<ExpenseSummaryDTO> report = expenseService.getExpenseReportByCategory();

        // Assert
        assertNotNull(report);
        assertEquals(2, report.size());
        assertEquals(ExpenseCategory.SEEDS, report.get(0).getCategory());
        assertEquals(new BigDecimal("45000.00"), report.get(0).getTotalAmount());
        verify(expenseRepository, times(1)).aggregateExpensesByCategory();
    }
}
