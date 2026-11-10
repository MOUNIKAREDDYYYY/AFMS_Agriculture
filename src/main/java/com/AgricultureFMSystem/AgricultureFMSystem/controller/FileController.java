package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FileResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

/**
 * ============================================================================================
 * Phase 4: File Handling REST Controller
 * ============================================================================================
 * Purpose: REST endpoints for:
 * - Uploading agricultural documents (soil test PDF, farm photos, receipts)
 * - Downloading / streaming uploaded files
 * - Exporting Harvest yield records to downloadable CSV
 * - Exporting Farm expenses to downloadable CSV
 * ============================================================================================
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "11. File Handling & CSV Exports", description = "Endpoints for document uploads, streaming downloads, and CSV data exports")
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * Upload a document or image (multipart/form-data)
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload agricultural document", description = "Uploads soil report, field photo, or receipt as multipart/form-data.")
    public ResponseEntity<FileResponseDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        String storedFileName = fileStorageService.storeFile(file);

        // Build download URI pointing to our download endpoint
        String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/")
                .path(storedFileName)
                .toUriString();

        FileResponseDTO response = FileResponseDTO.builder()
                .fileName(storedFileName)
                .originalFileName(file.getOriginalFilename())
                .fileDownloadUri(downloadUri)
                .fileType(file.getContentType())
                .size(file.getSize())
                .uploadTimestamp(LocalDateTime.now())
                .message("File uploaded successfully")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Download or stream a stored file
     */
    @GetMapping("/download/{fileName}")
    @Operation(summary = "Download file", description = "Streams a stored document by fileName.")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Export all Harvest records as a downloadable CSV file
     */
    @GetMapping("/export/harvests-csv")
    @Operation(summary = "Export harvest records to CSV", description = "Generates and streams a CSV file of all harvest yields and revenue.")
    public ResponseEntity<byte[]> exportHarvestsCsv() {
        byte[] csvData = fileStorageService.exportHarvestsToCsv();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"harvest_records_export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }

    /**
     * Export Farm expenses as a downloadable CSV file
     */
    @GetMapping("/export/expenses-csv")
    @Operation(summary = "Export farm expenses to CSV", description = "Generates and streams a CSV file of farm expenditures.")
    public ResponseEntity<byte[]> exportExpensesCsv(@RequestParam(required = false) Long farmId) {
        byte[] csvData = fileStorageService.exportExpensesToCsv(farmId);

        String fileName = (farmId != null) ? "expenses_farm_" + farmId + ".csv" : "all_farm_expenses.csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }
}