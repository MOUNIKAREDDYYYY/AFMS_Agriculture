package com.AgricultureFMSystem.AgricultureFMSystem.config;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.*;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.*;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * ============================================================================================
 * Phase 4 & 5: Application Data Initializer (CommandLineRunner with @Profile)
 * ============================================================================================
 * Seeds realistic agricultural demo data on startup in development mode ('dev' or 'default').
 * Will NOT seed demo data in 'prod' or 'test' environments to maintain data integrity.
 */
@Slf4j
@Configuration
@Profile({"dev", "default"})
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final FarmerRepository farmerRepository;
    private final FarmRepository farmRepository;
    private final FieldRepository fieldRepository;
    private final CropRepository cropRepository;
    private final CropPlanRepository cropPlanRepository;
    private final ActivityRepository activityRepository;
    private final ExpenseRepository expenseRepository;
    private final HarvestRepository harvestRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        // Ensure default authentication users always exist
        if (userRepository.count() == 0) {
            log.info("Seeding initial authentication users (admin, farmer_rajesh)...");
            User adminUser = User.builder()
                    .username("admin")
                    .email("admin@agriculturefms.com")
                    .password(com.AgricultureFMSystem.AgricultureFMSystem.service.impl.AuthServiceImpl.hashPassword("admin123"))
                    .fullName("System Administrator")
                    .role(UserRole.ADMIN)
                    .active(true)
                    .build();

            User farmerUser = User.builder()
                    .username("farmer_rajesh")
                    .email("rajesh.patel@agrofarm.com")
                    .password(com.AgricultureFMSystem.AgricultureFMSystem.service.impl.AuthServiceImpl.hashPassword("farmer123"))
                    .fullName("Rajesh Kumar Patel")
                    .role(UserRole.FARMER)
                    .active(true)
                    .build();

            userRepository.saveAll(List.of(adminUser, farmerUser));
            log.info("Default user accounts seeded successfully.");
        }

        if (farmerRepository.count() > 0) {
            log.info("Agricultural entities already seeded.");
            return;
        }

        log.info("Starting initial seed data population for Agriculture Farm Management System...");

        // 1. Seed Farmers
        Farmer farmer1 = Farmer.builder()
                .fullName("Rajesh Kumar Patel")
                .email("rajesh.patel@agrofarm.com")
                .phoneNumber("+91-9876543210")
                .address("Village Anand, District Kheda, Gujarat")
                .governmentIdNumber("AGRI-GJ-2024-0012")
                .active(true)
                .build();

        Farmer farmer2 = Farmer.builder()
                .fullName("Anita Ramesh Sharma")
                .email("anita.sharma@greenfields.org")
                .phoneNumber("+91-9823456789")
                .address("Sector 4, Karnal, Haryana")
                .governmentIdNumber("AGRI-HR-2023-8841")
                .active(true)
                .build();

        farmerRepository.saveAll(List.of(farmer1, farmer2));

        // 2. Seed Farms
        Farm farm1 = Farm.builder()
                .name("Sahyadri Agro Plantation & Nursery")
                .location("National Highway 48, Anand, Gujarat")
                .totalAreaInAcres(50.0)
                .farmer(farmer1)
                .build();

        Farm farm2 = Farm.builder()
                .name("Golden Harvest Valley")
                .location("Canal Road, Karnal, Haryana")
                .totalAreaInAcres(85.0)
                .farmer(farmer2)
                .build();

        Farm farm3 = Farm.builder()
                .name("Sahyadri Organic Plantation")
                .location("Trimbakeshwar Road, Nashik, Maharashtra")
                .totalAreaInAcres(65.0)
                .farmer(farmer1)
                .build();

        farmRepository.saveAll(List.of(farm1, farm2, farm3));

        // 3. Seed Crops
        Crop wheat = Crop.builder()
                .name("Wheat")
                .variety("Sharbati Premium")
                .category(CropCategory.GRAIN)
                .season(CropSeason.RABI)
                .growthDurationDays(130)
                .optimalTemperature("15C - 25C")
                .waterRequirement("Medium (450 mm)")
                .standardYieldPerAcre(18.5)
                .yieldUnit("QUINTAL")
                .build();

        Crop rice = Crop.builder()
                .name("Rice")
                .variety("Basmati 1121")
                .category(CropCategory.GRAIN)
                .season(CropSeason.KHARIF)
                .growthDurationDays(145)
                .optimalTemperature("22C - 32C")
                .waterRequirement("High (1200 mm)")
                .standardYieldPerAcre(22.0)
                .yieldUnit("QUINTAL")
                .build();

        Crop tomato = Crop.builder()
                .name("Tomato")
                .variety("Roma Hybrid")
                .category(CropCategory.VEGETABLE)
                .season(CropSeason.YEAR_ROUND)
                .growthDurationDays(85)
                .optimalTemperature("18C - 27C")
                .waterRequirement("Moderate (600 mm)")
                .standardYieldPerAcre(150.0)
                .yieldUnit("CRATE")
                .build();

        Crop cotton = Crop.builder()
                .name("Cotton")
                .variety("Bt Cotton Hybrid")
                .category(CropCategory.CASH_CROP)
                .season(CropSeason.KHARIF)
                .growthDurationDays(160)
                .optimalTemperature("25C - 35C")
                .waterRequirement("Medium (700 mm)")
                .standardYieldPerAcre(12.0)
                .yieldUnit("QUINTAL")
                .build();

        cropRepository.saveAll(List.of(wheat, rice, tomato, cotton));

        // 4. Seed Fields
        Field field1 = Field.builder()
                .name("North Plot - A1")
                .areaInAcres(15.0)
                .soilType("Alluvial Loam")
                .irrigationType("Drip Irrigation")
                .status(FieldStatus.PLANTED)
                .farm(farm1)
                .currentCrop(wheat)
                .build();

        Field field2 = Field.builder()
                .name("South Plot - B2")
                .areaInAcres(20.0)
                .soilType("Clay Loam")
                .irrigationType("Canal Flood Irrigation")
                .status(FieldStatus.PLANTED)
                .farm(farm1)
                .currentCrop(rice)
                .build();

        Field field3 = Field.builder()
                .name("Greenhouse Sector C")
                .areaInAcres(5.0)
                .soilType("Sandy Loam Enriched")
                .irrigationType("Micro Sprinkler")
                .status(FieldStatus.PLANTED)
                .farm(farm1)
                .currentCrop(tomato)
                .build();

        Field field4 = Field.builder()
                .name("East Acreage 1")
                .areaInAcres(40.0)
                .soilType("Black Cotton Soil")
                .irrigationType("Canal Flood")
                .status(FieldStatus.PLANTED)
                .farm(farm2)
                .currentCrop(cotton)
                .build();

        Field field5 = Field.builder()
                .name("West Acreage 2")
                .areaInAcres(45.0)
                .soilType("Black Cotton Soil")
                .irrigationType("Canal Flood Irrigation")
                .status(FieldStatus.PLANTED)
                .farm(farm2)
                .currentCrop(rice)
                .build();

        Field field6 = Field.builder()
                .name("Organic Orchard Plot A")
                .areaInAcres(25.0)
                .soilType("Alluvial Red Loam")
                .irrigationType("Drip Irrigation")
                .status(FieldStatus.PLANTED)
                .farm(farm3)
                .currentCrop(tomato)
                .build();

        Field field7 = Field.builder()
                .name("Bio-Dynamic Grain Block B")
                .areaInAcres(30.0)
                .soilType("Enriched Loam")
                .irrigationType("Sprinkler System")
                .status(FieldStatus.PLANTED)
                .farm(farm3)
                .currentCrop(wheat)
                .build();

        fieldRepository.saveAll(List.of(field1, field2, field3, field4, field5, field6, field7));

        // 5. Seed Crop Plans
        CropPlan plan1 = CropPlan.builder()
                .field(field1)
                .crop(wheat)
                .season(CropSeason.RABI)
                .plannedStartDate(LocalDate.now().minusMonths(3))
                .estimatedHarvestDate(LocalDate.now().plusMonths(1))
                .expectedYieldQuantity(275.0)
                .yieldUnit("QUINTAL")
                .estimatedBudget(new BigDecimal("120000.00"))
                .status(CropPlanStatus.IN_PROGRESS)
                .notes("Rabi wheat cycle with certified Sharbati seeds.")
                .build();

        CropPlan plan2 = CropPlan.builder()
                .field(field3)
                .crop(tomato)
                .season(CropSeason.YEAR_ROUND)
                .plannedStartDate(LocalDate.now().minusMonths(2))
                .estimatedHarvestDate(LocalDate.now().plusWeeks(2))
                .expectedYieldQuantity(750.0)
                .yieldUnit("CRATE")
                .estimatedBudget(new BigDecimal("65000.00"))
                .status(CropPlanStatus.IN_PROGRESS)
                .notes("Drip irrigated greenhouse tomato cultivation.")
                .build();

        cropPlanRepository.saveAll(List.of(plan1, plan2));

        // 6. Seed Activities
        Activity act1 = Activity.builder()
                .title("Tractor Deep Plouging and Leveling")
                .activityType(ActivityType.SOIL_PREPARATION)
                .status(ActivityStatus.COMPLETED)
                .scheduledDate(LocalDate.now().minusMonths(3))
                .completionDate(LocalDate.now().minusMonths(3).plusDays(2))
                .assignedTo("Mohan Singh")
                .notes("Done with John Deere 5050D tractor")
                .field(field1)
                .crop(wheat)
                .build();

        Activity act2 = Activity.builder()
                .title("Certified Seed Sowing")
                .activityType(ActivityType.PLANTING)
                .status(ActivityStatus.COMPLETED)
                .scheduledDate(LocalDate.now().minusMonths(3).plusDays(4))
                .completionDate(LocalDate.now().minusMonths(3).plusDays(4))
                .assignedTo("Suresh Patel")
                .notes("Sown at 40kg per acre")
                .field(field1)
                .crop(wheat)
                .build();

        Activity act3 = Activity.builder()
                .title("First NPK (12:32:16) Fertilizer Application")
                .activityType(ActivityType.FERTILIZING)
                .status(ActivityStatus.COMPLETED)
                .scheduledDate(LocalDate.now().minusMonths(2))
                .completionDate(LocalDate.now().minusMonths(2))
                .assignedTo("Mohan Singh")
                .notes("Applied 15 bags across Plot A1")
                .field(field1)
                .crop(wheat)
                .build();

        Activity act4 = Activity.builder()
                .title("Drip Fertigation & Pest Inspection")
                .activityType(ActivityType.PEST_CONTROL)
                .status(ActivityStatus.PLANNED)
                .scheduledDate(LocalDate.now().plusDays(2))
                .assignedTo("Vikram Dave")
                .notes("Inspect leaves for early blight symptoms")
                .field(field3)
                .crop(tomato)
                .build();

        activityRepository.saveAll(List.of(act1, act2, act3, act4));

        // 7. Seed Expenses
        Expense exp1 = Expense.builder()
                .title("15 Bags Sharbati Wheat Certified Seeds")
                .category(ExpenseCategory.SEEDS)
                .amount(new BigDecimal("18500.00"))
                .expenseDate(LocalDate.now().minusMonths(3).plusDays(2))
                .paymentMethod("BANK_TRANSFER")
                .vendorName("National Seeds Corporation")
                .farm(farm1)
                .field(field1)
                .crop(wheat)
                .build();

        Expense exp2 = Expense.builder()
                .title("NPK Complex & Urea Fertilizers")
                .category(ExpenseCategory.FERTILIZERS)
                .amount(new BigDecimal("24500.00"))
                .expenseDate(LocalDate.now().minusMonths(2))
                .paymentMethod("UPI")
                .vendorName("IFFCO Kisan Seva Kendra")
                .farm(farm1)
                .field(field1)
                .crop(wheat)
                .build();

        Expense exp3 = Expense.builder()
                .title("Labor charges for land preparation & sowing")
                .category(ExpenseCategory.LABOR)
                .amount(new BigDecimal("16000.00"))
                .expenseDate(LocalDate.now().minusMonths(3).plusDays(5))
                .paymentMethod("CASH")
                .vendorName("Local Farm Worker Union")
                .farm(farm1)
                .field(field1)
                .build();

        Expense exp4 = Expense.builder()
                .title("Tractor Diesel 120 Liters")
                .category(ExpenseCategory.FUEL)
                .amount(new BigDecimal("10800.00"))
                .expenseDate(LocalDate.now().minusMonths(3))
                .paymentMethod("UPI")
                .vendorName("Indian Oil Fuel Station")
                .farm(farm1)
                .build();

        Expense exp5 = Expense.builder()
                .title("Tomato Seedlings & Cocopeat Trays")
                .category(ExpenseCategory.SEEDS)
                .amount(new BigDecimal("14000.00"))
                .expenseDate(LocalDate.now().minusMonths(2).plusDays(1))
                .paymentMethod("BANK_TRANSFER")
                .vendorName("Green Tech Nursery")
                .farm(farm1)
                .field(field3)
                .crop(tomato)
                .build();

        // Expenses for Farm 2 (Anita Sharma)
        Expense exp6 = Expense.builder()
                .title("Purchased Bt Cotton Hybrid Seeds")
                .category(ExpenseCategory.SEEDS)
                .amount(new BigDecimal("32000.00"))
                .expenseDate(LocalDate.now().minusMonths(3))
                .paymentMethod("BANK_TRANSFER")
                .vendorName("Mahyco Seed Corp")
                .farm(farm2)
                .field(field4)
                .crop(cotton)
                .build();

        Expense exp7 = Expense.builder()
                .title("NPK Complex & Micronutrient Mix")
                .category(ExpenseCategory.FERTILIZERS)
                .amount(new BigDecimal("44500.00"))
                .expenseDate(LocalDate.now().minusMonths(2))
                .paymentMethod("UPI")
                .vendorName("Kisan Agri Centre")
                .farm(farm2)
                .field(field4)
                .crop(cotton)
                .build();

        Expense exp8 = Expense.builder()
                .title("Field Labor Wages for 15 workers")
                .category(ExpenseCategory.LABOR)
                .amount(new BigDecimal("38000.00"))
                .expenseDate(LocalDate.now().minusMonths(2))
                .paymentMethod("CASH")
                .vendorName("Karnal Agri Labor Union")
                .farm(farm2)
                .field(field4)
                .build();

        Expense exp9 = Expense.builder()
                .title("Tractor & Machinery Fuel 300L")
                .category(ExpenseCategory.FUEL)
                .amount(new BigDecimal("26500.00"))
                .expenseDate(LocalDate.now().minusMonths(3))
                .paymentMethod("UPI")
                .vendorName("HP Petrol Pump Karnal")
                .farm(farm2)
                .build();

        Expense exp10 = Expense.builder()
                .title("Integrated Pest Spray Chemicals")
                .category(ExpenseCategory.PESTICIDES)
                .amount(new BigDecimal("18000.00"))
                .expenseDate(LocalDate.now().minusMonths(1))
                .paymentMethod("BANK_TRANSFER")
                .vendorName("Agro Crop Care Ltd")
                .farm(farm2)
                .field(field4)
                .crop(cotton)
                .build();

        // Expenses for Farm 3 (Rajesh Patel)
        Expense exp11 = Expense.builder()
                .title("Vermicompost Bulk Delivery")
                .category(ExpenseCategory.FERTILIZERS)
                .amount(new BigDecimal("22000.00"))
                .expenseDate(LocalDate.now().minusMonths(2))
                .paymentMethod("BANK_TRANSFER")
                .vendorName("Green Bio Organics")
                .farm(farm3)
                .field(field6)
                .crop(tomato)
                .build();

        Expense exp12 = Expense.builder()
                .title("Organic Tomato Seedling Trays")
                .category(ExpenseCategory.SEEDS)
                .amount(new BigDecimal("18500.00"))
                .expenseDate(LocalDate.now().minusMonths(2))
                .paymentMethod("UPI")
                .vendorName("Sahyadri Bio Nursery")
                .farm(farm3)
                .field(field6)
                .crop(tomato)
                .build();

        Expense exp13 = Expense.builder()
                .title("Manual Organic Labor Charges")
                .category(ExpenseCategory.LABOR)
                .amount(new BigDecimal("24000.00"))
                .expenseDate(LocalDate.now().minusMonths(1))
                .paymentMethod("CASH")
                .vendorName("Nashik Farm Workers")
                .farm(farm3)
                .field(field6)
                .build();

        Expense exp14 = Expense.builder()
                .title("Solar Pump Maintenance & Filter Servicing")
                .category(ExpenseCategory.IRRIGATION)
                .amount(new BigDecimal("19000.00"))
                .expenseDate(LocalDate.now().minusMonths(1))
                .paymentMethod("BANK_TRANSFER")
                .vendorName("SunPower Agro Solutions")
                .farm(farm3)
                .build();

        expenseRepository.saveAll(List.of(exp1, exp2, exp3, exp4, exp5, exp6, exp7, exp8, exp9, exp10, exp11, exp12, exp13, exp14));

        // 8. Seed Harvests across all farms
        Harvest harvest1 = Harvest.builder()
                .harvestDate(LocalDate.now().minusMonths(1))
                .quantity(420.0)
                .unit("QUINTAL")
                .qualityGrade("Grade A Premium")
                .unitPrice(new BigDecimal("3200.00"))
                .totalRevenue(new BigDecimal("1344000.00"))
                .storageLocation("Warehouse Section 3, Anand Mandi")
                .notes("Sold at agricultural produce market committee (APMC). Excellent grain length.")
                .field(field2)
                .crop(rice)
                .build();

        Harvest harvest2 = Harvest.builder()
                .harvestDate(LocalDate.now().minusWeeks(1))
                .quantity(180.0)
                .unit("CRATE")
                .qualityGrade("Export Grade 1")
                .unitPrice(new BigDecimal("650.00"))
                .totalRevenue(new BigDecimal("117000.00"))
                .storageLocation("Cold Storage Unit A")
                .notes("First batch picking of greenhouse Roma tomatoes.")
                .field(field3)
                .crop(tomato)
                .build();

        Harvest harvest3 = Harvest.builder()
                .harvestDate(LocalDate.now().minusWeeks(2))
                .quantity(340.0)
                .unit("QUINTAL")
                .qualityGrade("Grade A Long Staple")
                .unitPrice(new BigDecimal("7100.00"))
                .totalRevenue(new BigDecimal("2414000.00"))
                .storageLocation("Cotton Corporation Warehouse, Karnal")
                .notes("High-grade raw cotton reaped from East Acreage 1.")
                .field(field4)
                .crop(cotton)
                .build();

        Harvest harvest4 = Harvest.builder()
                .harvestDate(LocalDate.now().minusWeeks(3))
                .quantity(280.0)
                .unit("QUINTAL")
                .qualityGrade("Premium Export 1121")
                .unitPrice(new BigDecimal("3500.00"))
                .totalRevenue(new BigDecimal("980000.00"))
                .storageLocation("Central Grain Silo Karnal")
                .notes("Export grade Basmati paddy harvest from West Acreage 2.")
                .field(field5)
                .crop(rice)
                .build();

        Harvest harvest5 = Harvest.builder()
                .harvestDate(LocalDate.now().minusWeeks(1))
                .quantity(520.0)
                .unit("CRATE")
                .qualityGrade("Certified Organic Grade A")
                .unitPrice(new BigDecimal("750.00"))
                .totalRevenue(new BigDecimal("390000.00"))
                .storageLocation("Cold Storage Unit 2, Nashik")
                .notes("Organic vine-ripened tomatoes for premium retail.")
                .field(field6)
                .crop(tomato)
                .build();

        Harvest harvest6 = Harvest.builder()
                .harvestDate(LocalDate.now().minusWeeks(2))
                .quantity(220.0)
                .unit("QUINTAL")
                .qualityGrade("Organic Sharbati Premium")
                .unitPrice(new BigDecimal("4100.00"))
                .totalRevenue(new BigDecimal("902000.00"))
                .storageLocation("Organic Produce Hub Mandi")
                .notes("Organic wheat sold to organic grain mills.")
                .field(field7)
                .crop(wheat)
                .build();

        harvestRepository.saveAll(List.of(harvest1, harvest2, harvest3, harvest4, harvest5, harvest6));

        log.info("Agriculture Farm Management System complete demo dataset loaded successfully!");
    }
}
