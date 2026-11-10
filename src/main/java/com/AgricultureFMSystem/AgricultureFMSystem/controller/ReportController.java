package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmFinancialSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmerFinancialSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FieldHistoryResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints for farm and farmer financial statements, profitability, and field lifecycle histories.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "9. Comprehensive Farm Reports", description = "Endpoints for farm and farmer financial statements, profitability, and field lifecycle histories")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/financial/farm/{farmId}")
    @Operation(summary = "Farm financial report", description = "Calculates total expenses, total harvest revenues, and net profit/loss for a farm.")
    public ResponseEntity<FarmFinancialSummaryDTO> getFarmFinancialReport(@PathVariable Long farmId) {
        return ResponseEntity.ok(reportService.getFarmFinancialReport(farmId));
    }

    @GetMapping("/financial/all-farms")
    @Operation(summary = "All farms financial summary", description = "Generates profitability summaries for all farms in the system.")
    public ResponseEntity<List<FarmFinancialSummaryDTO>> getAllFarmsFinancialSummary() {
        return ResponseEntity.ok(reportService.getAllFarmsFinancialSummary());
    }

    @GetMapping("/financial/farmer/{farmerId}")
    @Operation(summary = "Farmer consolidated financial report", description = "Aggregates revenue, expenses, and net profit across all farms owned by the farmer.")
    public ResponseEntity<FarmerFinancialSummaryDTO> getFarmerFinancialReport(@PathVariable Long farmerId) {
        return ResponseEntity.ok(reportService.getFarmerFinancialReport(farmerId));
    }

    @GetMapping("/financial/all-farmers")
    @Operation(summary = "All farmers financial summary", description = "Generates profitability and production summaries for every farmer in the cooperative.")
    public ResponseEntity<List<FarmerFinancialSummaryDTO>> getAllFarmersFinancialSummary() {
        return ResponseEntity.ok(reportService.getAllFarmersFinancialSummary());
    }

    @GetMapping("/field/{fieldId}/history")
    @Operation(summary = "Field lifecycle history", description = "Fetches complete timeline history of crop plans, activities, expenses, and harvests for a field.")
    public ResponseEntity<FieldHistoryResponseDTO> getFieldHistory(@PathVariable Long fieldId) {
        return ResponseEntity.ok(reportService.getFieldHistory(fieldId));
    }
}
