package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmFinancialSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmerFinancialSummaryDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FieldHistoryResponseDTO;

import java.util.List;

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

    FieldHistoryResponseDTO getFieldHistory(Long fieldId);
}
