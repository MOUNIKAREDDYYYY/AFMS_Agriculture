package com.AgricultureFMSystem.AgricultureFMSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================================================
 * Phase 2: Spring Boot Application Entry Point
 * ============================================================================================
 * Agriculture Farm Management System (AFMS)
 * 
 * Scenario:
 * Farmers manage crops, fields, expenses, scheduled farming activities, and
 * harvest records.
 * 
 * Relationships:
 * - Farmer -> Farms (One-to-Many)
 * - Farm -> Fields (One-to-Many)
 * - Field -> Crops (Many-to-One) & Activities (One-to-Many)
 * - Farm/Field/Crop/Activity -> Expenses
 * - Field/Crop -> Harvests
 * 
 * Key Highlights:
 * - Phase 1: Lombok (@Getter, @Setter, @Builder, @RequiredArgsConstructor),
 * SLF4J logging (@Slf4j)
 * - Phase 2: Spring MVC, REST APIs, Jakarta Validation (@Valid),
 * GlobalExceptionHandler (@RestControllerAdvice)
 * - Phase 3: Spring Data JPA, Hibernate ORM, Relationships, Aggregations,
 * Transactions (@Transactional)
 * - Phase 4: OpenAPI/Swagger Documentation, Logging, File Handling, HikariCP,
 * Scheduler, Actuator, DTO Pattern
 * - Phase 5: Profiles (dev/prod/test), JUnit 5 & Mockito Testing, Caching,
 * Security (JWT)
 */
@SpringBootApplication
public class AgricultureFmSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgricultureFmSystemApplication.class, args);
	}

}
