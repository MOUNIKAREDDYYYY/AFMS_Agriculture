package com.AgricultureFMSystem.AgricultureFMSystem.actuator;

import com.AgricultureFMSystem.AgricultureFMSystem.repository.CropRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FarmRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FarmerRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FieldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * ============================================================================================
 * Phase 4: Spring Boot Actuator Custom Health Indicator
 * ============================================================================================
 * Purpose: Custom health check monitoring the agricultural ecosystem vitality and database availability.
 * Checks total farmers, active farms, crop catalog, and cultivated fields.
 * Returns UP if core entities exist and queries succeed; DOWN if database connectivity fails.
 * ============================================================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgriculturalHealthIndicator implements HealthIndicator {

    // Inject repositories to verify database connectivity and fetch entity metrics
    private final FarmerRepository farmerRepository;
    private final FarmRepository farmRepository;
    private final FieldRepository fieldRepository;
    private final CropRepository cropRepository;

    /**
     * Health evaluation method invoked by Spring Boot Actuator (/actuator/health)
     */
    @Override
    public Health health() {
        try {
            // Perform count queries to test DB responsiveness and collect telemetry
            long farmerCount = farmerRepository.count();
            long farmCount = farmRepository.count();
            long fieldCount = fieldRepository.count();
            long cropCount = cropRepository.count();

            // Determine status: If database responds successfully, return status UP with details
            return Health.up()
                    .withDetail("service", "Agriculture Farm Management System (AFMS)")
                    .withDetail("databaseStatus", "ONLINE")
                    .withDetail("totalFarmers", farmerCount)
                    .withDetail("totalFarms", farmCount)
                    .withDetail("totalFields", fieldCount)
                    .withDetail("totalCropsRegistered", cropCount)
                    .withDetail("systemReadiness", farmerCount > 0 ? "OPERATIONAL_WITH_DATA" : "INITIALIZING")
                    .build();

        } catch (Exception ex) {
            // In case of database error or network failure, report status DOWN with error details
            log.error("[Actuator] Health check failed: Unable to connect to Agriculture database: {}", ex.getMessage());
            return Health.down()
                    .withDetail("service", "Agriculture Farm Management System (AFMS)")
                    .withDetail("databaseStatus", "OFFLINE")
                    .withDetail("errorMessage", ex.getMessage())
                    .build();
        }
    }
}