package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.Expense;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Harvest;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.BadRequestException;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.ExpenseRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.HarvestRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * ============================================================================================
 * Phase 4: File Handling - FileStorageServiceImpl
 * ============================================================================================
 * // Purpose: Handles physical disk storage of agronomic files and CSV report generation.
 * //
 * // Concepts demonstrated:
 * // - MultipartFile: Processing file streams from HTTP POST multipart/form-data
 * // - java.nio.file.Path / Files: Secure storage directory creation and file copying
 * // - Path Traversal Prevention: StringUtils.cleanPath and path validation
 * // - Resource / UrlResource: Returning file resources for binary streaming
 * // - CSV Byte Streaming: Writing tabular harvest and expense records to CSV bytes
 * ============================================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileStorageServiceImpl implements FileStorageService {

    // // Target directory where uploaded agricultural documents are stored
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    private final HarvestRepository harvestRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * // Initialize the storage directory upon bean creation
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("// [FileHandling] Initialized uploads storage directory at: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("// [FileHandling] Could not create upload directory: {}", ex.getMessage());
            throw new RuntimeException("Could not create upload directory!", ex);
        }
    }

    /**
     * // Stores a multipart file with security sanitization and UUID prefix
     */
    @Override
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Failed to store empty file.");
        }

        // // Clean filename and validate against directory traversal attacks
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        if (originalFileName.contains("..")) {
            throw new BadRequestException("Invalid filename with path traversal sequence: " + originalFileName);
        }

        // // Generate unique filename to avoid collision on disk
        String storedFileName = UUID.randomUUID().toString().substring(0, 8) + "_" + originalFileName;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            // // Copy file input stream to server disk, replacing existing if present
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("// [FileHandling] Successfully stored file '{}' as '{}'", originalFileName, storedFileName);
            return storedFileName;
        } catch (IOException ex) {
            log.error("// [FileHandling] Failed to store file {}: {}", storedFileName, ex.getMessage());
            throw new RuntimeException("Failed to store file " + storedFileName, ex);
        }
    }

    /**
     * // Loads a file as a Spring Resource for download
     */
    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File", "fileName", fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File", "fileName", fileName);
        }
    }

    /**
     * // Generates a CSV file containing all Harvest yield and revenue records
     */
    @Override
    public byte[] exportHarvestsToCsv() {
        log.info("// [FileHandling] Generating CSV export for all harvest records");
        List<Harvest> harvests = harvestRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
            // // CSV Header row
            writer.println("HarvestID,HarvestDate,FarmName,FieldCode,CropName,Quantity,Unit,QualityGrade,UnitPrice,TotalRevenue,StorageLocation");

            // // CSV Data rows
            for (Harvest h : harvests) {
                String farmName = (h.getField() != null && h.getField().getFarm() != null)
                        ? h.getField().getFarm().getName().replace(",", " ") : "N/A";
                String fieldName = h.getField() != null ? h.getField().getName().replace(",", " ") : "N/A";
                String cropName = h.getCrop() != null ? h.getCrop().getName() : "N/A";

                writer.printf("%d,%s,%s,%s,%s,%.2f,%s,%s,%.2f,%.2f,%s%n",
                        h.getId(),
                        h.getHarvestDate(),
                        farmName,
                        fieldName,
                        cropName,
                        h.getQuantity() != null ? h.getQuantity() : 0.0,
                        h.getUnit() != null ? h.getUnit() : "N/A",
                        h.getQualityGrade() != null ? h.getQualityGrade().replace(",", " ") : "Standard",
                        h.getUnitPrice() != null ? h.getUnitPrice() : 0.0,
                        h.getTotalRevenue() != null ? h.getTotalRevenue() : 0.0,
                        h.getStorageLocation() != null ? h.getStorageLocation().replace(",", " ") : "Main Silo"
                );
            }
            writer.flush();
        }

        return baos.toByteArray();
    }

    /**
     * // Generates a CSV file of Farm Expenditures
     */
    @Override
    public byte[] exportExpensesToCsv(Long farmId) {
        log.info("// [FileHandling] Generating CSV export for farm expenditures (farmId: {})", farmId);
        List<Expense> expenses = (farmId != null)
                ? expenseRepository.findByFarmId(farmId)
                : expenseRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
            // // CSV Header row
            writer.println("ExpenseID,ExpenseDate,FarmName,Category,Amount,PaymentMethod,VendorName,Title,Notes");

            // // CSV Data rows
            for (Expense e : expenses) {
                String farmName = e.getFarm() != null ? e.getFarm().getName().replace(",", " ") : "N/A";
                String vendor = e.getVendorName() != null ? e.getVendorName().replace(",", " ") : "Vendor";
                String title = e.getTitle() != null ? e.getTitle().replace(",", " ") : "Agro expense";
                String notes = e.getNotes() != null ? e.getNotes().replace(",", " ") : "N/A";

                writer.printf("%d,%s,%s,%s,%.2f,%s,%s,%s,%s%n",
                        e.getId(),
                        e.getExpenseDate(),
                        farmName,
                        e.getCategory(),
                        e.getAmount() != null ? e.getAmount() : 0.0,
                        e.getPaymentMethod() != null ? e.getPaymentMethod() : "BANK_TRANSFER",
                        vendor,
                        title,
                        notes
                );
            }
            writer.flush();
        }

        return baos.toByteArray();
    }
}
