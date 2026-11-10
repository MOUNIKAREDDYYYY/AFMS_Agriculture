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
import org.springframework.beans.factory.annotation.Value;
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
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Handles physical disk storage of agronomic files and RFC-4180 CSV exports.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir = "uploads";

    private Path fileStorageLocation;

    private final HarvestRepository harvestRepository;
    private final ExpenseRepository expenseRepository;

    @PostConstruct
    public void init() {
        try {
            this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.fileStorageLocation);
            log.info("[FileHandling] Initialized uploads storage directory at: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("[FileHandling] Could not create upload directory: {}", ex.getMessage());
            throw new RuntimeException("Could not create upload directory!", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Failed to store empty file.");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        if (originalFileName.contains("..")) {
            throw new BadRequestException("Invalid filename with path traversal sequence: " + originalFileName);
        }

        String storedFileName = UUID.randomUUID().toString().substring(0, 8) + "_" + originalFileName;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("[FileHandling] Successfully stored file '{}' as '{}'", originalFileName, storedFileName);
            return storedFileName;
        } catch (IOException ex) {
            log.error("[FileHandling] Failed to store file {}: {}", storedFileName, ex.getMessage());
            throw new RuntimeException("Failed to store file " + storedFileName, ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new BadRequestException("Invalid file path: " + fileName);
            }
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

    @Override
    public byte[] exportHarvestsToCsv() {
        log.info("[FileHandling] Generating CSV export for all harvest records");
        List<Harvest> harvests = harvestRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
            writer.println("HarvestID,HarvestDate,FarmName,FieldCode,CropName,Quantity,Unit,QualityGrade,UnitPrice,TotalRevenue,StorageLocation");

            for (Harvest h : harvests) {
                String farmName = (h.getField() != null && h.getField().getFarm() != null)
                        ? h.getField().getFarm().getName() : "N/A";
                String fieldName = h.getField() != null ? h.getField().getName() : "N/A";
                String cropName = h.getCrop() != null ? h.getCrop().getName() : "N/A";

                writer.printf("%d,%s,%s,%s,%s,%.2f,%s,%s,%.2f,%.2f,%s%n",
                        h.getId(),
                        h.getHarvestDate(),
                        escapeCsv(farmName),
                        escapeCsv(fieldName),
                        escapeCsv(cropName),
                        h.getQuantity() != null ? h.getQuantity() : 0.0,
                        escapeCsv(h.getUnit() != null ? h.getUnit() : "N/A"),
                        escapeCsv(h.getQualityGrade() != null ? h.getQualityGrade() : "Standard"),
                        h.getUnitPrice() != null ? h.getUnitPrice() : BigDecimal.ZERO,
                        h.getTotalRevenue() != null ? h.getTotalRevenue() : BigDecimal.ZERO,
                        escapeCsv(h.getStorageLocation() != null ? h.getStorageLocation() : "Main Silo")
                );
            }
            writer.flush();
        }

        return baos.toByteArray();
    }

    @Override
    public byte[] exportExpensesToCsv(Long farmId) {
        log.info("[FileHandling] Generating CSV export for farm expenditures (farmId: {})", farmId);
        List<Expense> expenses = (farmId != null)
                ? expenseRepository.findByFarmId(farmId)
                : expenseRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
            writer.println("ExpenseID,ExpenseDate,FarmName,Category,Amount,PaymentMethod,VendorName,Title,Notes");

            for (Expense e : expenses) {
                String farmName = e.getFarm() != null ? e.getFarm().getName() : "N/A";
                String vendor = e.getVendorName() != null ? e.getVendorName() : "Vendor";
                String title = e.getTitle() != null ? e.getTitle() : "Agro expense";
                String notes = e.getNotes() != null ? e.getNotes() : "N/A";

                writer.printf("%d,%s,%s,%s,%.2f,%s,%s,%s,%s%n",
                        e.getId(),
                        e.getExpenseDate(),
                        escapeCsv(farmName),
                        e.getCategory(),
                        e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO,
                        escapeCsv(e.getPaymentMethod() != null ? e.getPaymentMethod() : "BANK_TRANSFER"),
                        escapeCsv(vendor),
                        escapeCsv(title),
                        escapeCsv(notes)
                );
            }
            writer.flush();
        }

        return baos.toByteArray();
    }

    private static String escapeCsv(String raw) {
        if (raw == null) {
            return "";
        }
        String sanitized = raw;
        if (!sanitized.isEmpty()) {
            char first = sanitized.charAt(0);
            if (first == '=' || first == '+' || first == '-' || first == '@') {
                sanitized = "'" + sanitized;
            }
        }
        boolean needsQuotes = sanitized.contains(",") || sanitized.contains("\"")
                || sanitized.contains("\n") || sanitized.contains("\r");
        if (needsQuotes) {
            return "\"" + sanitized.replace("\"", "\"\"") + "\"";
        }
        return sanitized;
    }
}
