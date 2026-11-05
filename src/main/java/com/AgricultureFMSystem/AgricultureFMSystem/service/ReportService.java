package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmFinancialSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmerFinancialSummaryDTO;

import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 3 & 4: Agronomic & Financial Reporting Service
 * ============================================================================================
 */
public interface ReportService {

    FarmFinancialSummaryDTO getFarmFinancialReport(Long farmId);

    List<FarmFinancialSummaryDTO> getAllFarmsFinancialSummary();

    FarmerFinancialSummaryDTO getFarmerFinancialReport(Long farmerId);

    List<FarmerFinancialSummaryDTO> getAllFarmersFinancialSummary();

    Map<String, Object> getFieldHistory(Long fieldId);
}
