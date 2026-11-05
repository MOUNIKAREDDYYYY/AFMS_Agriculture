package com.AgricultureFMSystem.AgricultureFMSystem.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * ============================================================================================
 * Phase 4: File Handling - FileStorageService Interface
 * ============================================================================================
 * // Purpose: Declares contract for file uploads (soil tests, receipts, field photos)
 * // and dynamic CSV report generation/streaming.
 * ============================================================================================
 */
public interface FileStorageService {

    /**
     * // Stores an uploaded Multipart file onto server storage with UUID sanitization
     */
    String storeFile(MultipartFile file);

    /**
     * // Loads a stored file as a Spring Resource for download streaming
     */
    Resource loadFileAsResource(String fileName);

    /**
     * // Exports all harvest yield and revenue records to CSV byte stream
     */
    byte[] exportHarvestsToCsv();

    /**
     * // Exports agricultural farm expenditure records to CSV byte stream
     */
    byte[] exportExpensesToCsv(Long farmId);
}
