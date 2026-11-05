package com.AgricultureFMSystem.AgricultureFMSystem.actuator;

import com.AgricultureFMSystem.AgricultureFMSystem.repository.ActivityRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.CropRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FarmRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FarmerRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.HarvestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 4: Spring Boot Actuator Custom Info Contributor
 * ============================================================================================
 * // Purpose: Augments the /actuator/info endpoint with live enterprise agricultural metrics.
 * // Displays real-time database totals, recorded yields, and agricultural platform configurations.
 * ============================================================================================
 */
@Component
@RequiredArgsConstructor
public class AgriculturalInfoContributor implements InfoContributor {

    // // Repository dependencies to collect live telemetry for the info endpoint
    private final FarmerRepository farmerRepository;
    private final FarmRepository farmRepository;
    private final CropRepository cropRepository;
    private final ActivityRepository activityRepository;
    private final HarvestRepository harvestRepository;

    /**
     * // Contributes dynamic operational metrics into Actuator's /actuator/info payload
     */
    @Override
    public void contribute(Info.Builder builder) {
        // // Map to hold business-specific statistics
        Map<String, Object> agroDetails = new LinkedHashMap<>();

        // // Agricultural module counters
        agroDetails.put("platformName", "Precision Agriculture Farm Management System");
        agroDetails.put("registeredFarmers", farmerRepository.count());
        agroDetails.put("activeFarms", farmRepository.count());
        agroDetails.put("registeredCrops", cropRepository.count());
        agroDetails.put("totalFarmActivities", activityRepository.count());
        agroDetails.put("totalHarvestEvents", harvestRepository.count());

        // // Calculate overall harvest revenue
        BigDecimal totalRevenue = harvestRepository.calculateTotalRevenueAcrossAllFarms();
        agroDetails.put("totalSystemHarvestRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        agroDetails.put("currency", "INR (₹)");
        agroDetails.put("environment", "Development / Production Multi-Profile Ready");

        // // Attach to Actuator Info builder under the 'agriculture-system' namespace
        builder.withDetail("agriculture-system", agroDetails);
    }
}
