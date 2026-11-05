package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmFinancialSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmerFinancialSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2, 3 & 4: Agronomic & Financial Reporting REST Controller
 * ============================================================================================
 * Endpoints:
 * - GET /api/reports/financial/farm/{farmId}    -> Comprehensive farm revenue vs expense & net profit
 * - GET /api/reports/financial/all-farms        -> Financial summary across all farms
 * - GET /api/reports/financial/farmer/{farmerId}-> Aggregated financial statement for a Farmer across all farms
 * - GET /api/reports/financial/all-farmers      -> Financial profitability summary for all Farmers
 * - GET /api/reports/field/{fieldId}/history    -> Complete history of field (plans, activities, expenses, harvests)
 */
@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "9. Comprehensive Farm Reports", description = "Endpoints for farm and farmer financial statements, profitability, and field lifecycle histories")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/financial/farm/{farmId}")
    @Operation(summary = "Farm financial report", description = "Calculates total expenses, total harvest revenues, and net profit/loss for a farm.")
    public ResponseEntity<FarmFinancialSummaryDTO> getFarmFinancialReport(@PathVariable Long farmId) {
        log.info("REST request for financial report of farm ID: {}", farmId);
        return ResponseEntity.ok(reportService.getFarmFinancialReport(farmId));
    }

    @GetMapping("/financial/all-farms")
    @Operation(summary = "All farms financial summary", description = "Generates profitability summaries for all farms in the system.")
    public ResponseEntity<List<FarmFinancialSummaryDTO>> getAllFarmsFinancialSummary() {
        log.info("REST request for all farms financial summary");
        return ResponseEntity.ok(reportService.getAllFarmsFinancialSummary());
    }

    @GetMapping("/financial/farmer/{farmerId}")
    @Operation(summary = "Farmer consolidated financial report", description = "Aggregates revenue, expenses, and net profit across all farms owned by the farmer.")
    public ResponseEntity<FarmerFinancialSummaryDTO> getFarmerFinancialReport(@PathVariable Long farmerId) {
        log.info("REST request for financial report of farmer ID: {}", farmerId);
        return ResponseEntity.ok(reportService.getFarmerFinancialReport(farmerId));
    }

    @GetMapping("/financial/all-farmers")
    @Operation(summary = "All farmers financial summary", description = "Generates profitability and production summaries for every farmer in the cooperative.")
    public ResponseEntity<List<FarmerFinancialSummaryDTO>> getAllFarmersFinancialSummary() {
        log.info("REST request for all farmers financial summary");
        return ResponseEntity.ok(reportService.getAllFarmersFinancialSummary());
    }

    @GetMapping("/field/{fieldId}/history")
    @Operation(summary = "Field lifecycle history", description = "Fetches complete timeline history of crop plans, activities, expenses, and harvests for a field.")
    public ResponseEntity<Map<String, Object>> getFieldHistory(@PathVariable Long fieldId) {
        log.info("REST request for history of field ID: {}", fieldId);
        return ResponseEntity.ok(reportService.getFieldHistory(fieldId));
    }
}
