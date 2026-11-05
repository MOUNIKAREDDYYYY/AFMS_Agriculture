# Agriculture Farm Management System (AFMS)

An enterprise-grade, Spring Boot-based Agriculture Farm Management System designed for farmers, agronomists, and farm supervisors to manage crop planning, plot tracking, daily activities, financial expenditures, and harvest yield records.

---

## 🌾 Scenario & Domain Model

- **Farmer**: Registered agricultural producer managing one or more farms.
- **Farm**: Physical agricultural estate/land holding located in a geographical region.
- **Field**: Individual plot/parcel within a farm characterized by soil type, acreage, irrigation system, and status (`AVAILABLE`, `PREPARATION`, `PLANTED`, `FALLOW`).
- **Crop**: Agronomic crop specifications (grain, pulse, vegetable, cash crop) including growth cycle duration, season (`KHARIF`, `RABI`, `ZAID`), and expected yields.
- **CropPlan**: Advanced crop planning schedule for a field with start date, target harvest date, expected yields, and budget.
- **Activity**: Field operations (soil preparation, planting, irrigation, fertilizing, pest spraying, weeding, harvesting) with schedules, personnel assignments, and completion tracking.
- **Expense**: Financial expenditures incurred across categories (`SEEDS`, `FERTILIZERS`, `PESTICIDES`, `LABOR`, `MACHINERY`, `FUEL`, `IRRIGATION`), linked to farms, fields, and crops.
- **Harvest**: Production yield records tracking reaped quantities, units (`KG`, `QUINTAL`, `TON`, `CRATE`), quality grades, unit sale prices, and total revenues.

---

## 🔗 JPA Entity Relationships

```
Farmer (1) ──────────< (M) Farm (1) ──────────< (M) Field
                                                      │
         ┌───────────────────┬────────────────────────┼────────────────────────┐
         │                   │                        │                        │
         ▼                   ▼                        ▼                        ▼
     CropPlan (M)       Activity (M)             Expense (M)              Harvest (M)
         │                   │                        │                        │
         └─────────► Crop ◄──┴────────────────────────┴────────────────────────┘
```

- **Farmer → Farms**: `@OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)`
- **Farm → Fields**: `@OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)`
- **Field → Crop**: `@ManyToOne(fetch = FetchType.LAZY)` (Current active crop)
- **Field → Activities**: `@OneToMany` (Lifecycle field operations)
- **Farm / Field / Crop / Activity → Expense**: Tracks expenditures at any granularity.
- **Field / Crop → Harvest**: Production records and revenues.

---

## 🏛️ Project Architecture & Package Structure

```
com.AgricultureFMSystem.AgricultureFMSystem
│
├── config/             # OpenAPI/Swagger, JPA Auditing, Task Scheduler, DataInitializer
├── controller/         # REST API Controllers (CRUD + Business APIs + Reports)
├── dto/
│   ├── request/        # Jakarta Bean Validation (@Valid) Request DTOs
│   └── response/       # Response DTOs, Aggregation DTOs, PagedResponse<T>
├── entity/             # JPA Entities inheriting BaseEntity (@MappedSuperclass)
│   └── enums/          # Status & category enumerations
├── exception/          # Custom exceptions & @RestControllerAdvice (GlobalExceptionHandler)
├── mapper/             # Entity <-> DTO transformation mappers
├── repository/         # Spring Data JPA Repositories & JPQL Aggregation Queries
├── scheduler/          # Background Task Scheduler for overdue & pending activities
└── service/
    └── impl/           # Business logic & @Transactional service implementations
```

---

## 🚀 Suggested Learning Order Mapping

### Phase 1: Java Frameworks, Spring Core, AOP, Lombok & Logging
- **Lombok**: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@RequiredArgsConstructor` eliminating boilerplate.
- **SLF4J / Logback**: `@Slf4j` instrumentation across controllers, services, schedulers, and exception handlers.

### Phase 2: Spring Boot Fundamentals, MVC, Validation & Global Exception Handling
- **REST Endpoints**: `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping`, `@DeleteMapping`.
- **Validation**: Jakarta `@Valid`, `@NotBlank`, `@NotNull`, `@Email`, `@Positive`, `@Size`, `@Pattern`.
- **Global Exception Handling**: Centralized `@RestControllerAdvice` translating validation errors and custom exceptions (`ResourceNotFoundException`, `BadRequestException`, `DuplicateResourceException`) into uniform `ErrorResponse` JSON.

### Phase 3: Spring Data JPA, Hibernate, Relationships & Transactions
- **Relationships**: Bidirectional and unidirectional mappings with `FetchType.LAZY`.
- **Transactions**: `@Transactional(readOnly = true)` on classes with write operations scoped via `@Transactional`.
- **Auditing**: `@CreatedDate` and `@LastModifiedDate` with `@EnableJpaAuditing`.
- **Aggregation Queries**:
  - `aggregateExpensesByCategory()`: `SUM(amount)`, `COUNT(id)`, `AVG(amount)` grouped by `ExpenseCategory`.
  - `aggregateHarvestsByCrop()`: `SUM(quantity)`, `SUM(totalRevenue)` grouped by `Crop`.
  - `sumExpensesByFarmId()`, `sumHarvestRevenueByFarmId()`.
- **Date Filtering**: JPQL queries with `BETWEEN :startDate AND :endDate`.

### Phase 4: Swagger / OpenAPI, HikariCP, Schedulers, Actuator & DTO Pattern
- **Swagger / OpenAPI**: Interactive documentation and API testing sandbox at `http://localhost:8080/swagger-ui/index.html`.
- **HikariCP**: High-performance connection pool configuration in `application.properties`.
- **Task Scheduler**: `@Scheduled` hourly background job alerting on overdue or due-today farming activities.
- **Actuator**: Health, info, and metrics endpoints at `http://localhost:8080/actuator`.
- **DTO Pattern**: Clean decoupling of internal database entities from external API contracts.

### Phase 5: Profiles, Testing & Mockito
- **JUnit 5 & Mockito**: Full unit test coverage in `src/test/java` testing service isolation, mock repositories, and error assertions.

---

## 📡 API Endpoints Overview

### 1. Farmer API (`/api/farmers`)
- `POST /api/farmers` — Register farmer (`@Valid`)
- `GET /api/farmers` — List farmers (paginated, sorted, optional `?active=true`)
- `GET /api/farmers/{id}` — Get farmer by ID
- `GET /api/farmers/search?query=patel` — Case-insensitive search
- `PUT /api/farmers/{id}` — Full update
- `PATCH /api/farmers/{id}` — Partial update
- `PATCH /api/farmers/{id}/status` — Toggle active status
- `DELETE /api/farmers/{id}` — Delete farmer

### 2. Farm API (`/api/farms`)
- `POST /api/farms` — Create farm linked to a farmer
- `GET /api/farms?farmerId=1` — List farms with optional farmer filter
- `GET /api/farms/{id}` — Get farm by ID
- `GET /api/farms/search?query=organic` — Search farms
- `PUT /api/farms/{id}` — Full update
- `PATCH /api/farms/{id}` — Partial update
- `DELETE /api/farms/{id}` — Delete farm

### 3. Field API (`/api/fields`)
- `POST /api/fields` — Add field to farm
- `GET /api/fields?farmId=1&status=AVAILABLE` — List/filter fields
- `GET /api/fields/{id}` — Get field by ID
- `GET /api/fields/search?query=alluvial` — Search fields
- `PATCH /api/fields/{id}/status?status=PLANTED` — Update field status
- `POST /api/fields/{id}/assign-crop?cropId=1` — Assign crop to field
- `PUT /api/fields/{id}` — Full update
- `DELETE /api/fields/{id}` — Delete field

### 4. Crop API (`/api/crops`)
- `POST /api/crops` — Register crop specifications
- `GET /api/crops?category=GRAIN&season=RABI` — List crops with category/season filter
- `GET /api/crops/{id}` — Get crop by ID
- `GET /api/crops/search?query=wheat` — Search crops
- `PUT /api/crops/{id}` — Full update
- `DELETE /api/crops/{id}` — Delete crop

### 5. Crop Planning API (`/api/crop-plans`)
- `POST /api/crop-plans` — Create crop plan
- `GET /api/crop-plans?fieldId=1&status=IN_PROGRESS` — List crop plans
- `GET /api/crop-plans/date-range?startDate=2026-01-01&endDate=2026-12-31` — Date filtering
- `POST /api/crop-plans/{id}/approve` — Approve plan
- `PATCH /api/crop-plans/{id}/status?status=COMPLETED` — Update plan status
- `POST /api/crop-plans/{id}/cancel` — Cancel plan

### 6. Activity Tracking API (`/api/activities`)
- `POST /api/activities` — Schedule farming activity
- `GET /api/activities?fieldId=1&status=PLANNED` — List activities
- `GET /api/activities/pending` — List all pending activities (PLANNED / IN_PROGRESS)
- `GET /api/activities/date-range?startDate=...&endDate=...` — Filter scheduled dates
- `POST /api/activities/{id}/complete?completionDate=2026-11-05&notes=Done` — Mark completed
- `POST /api/activities/{id}/cancel` — Cancel activity

### 7. Expense Reports API (`/api/expenses`)
- `POST /api/expenses` — Log expenditure
- `GET /api/expenses?farmId=1&category=FERTILIZERS` — List expenses
- `GET /api/expenses/date-range?startDate=...&endDate=...` — Date range filter
- `GET /api/expenses/report/by-category` — **Aggregation Report**: Total amount & count grouped by category
- `GET /api/expenses/report/filtered?farmId=1` — Filtered aggregation report
- `GET /api/expenses/report/farm-total?farmId=1` — Total farm expenditure

### 8. Harvest Records API (`/api/harvests`)
- `POST /api/harvests` — Record harvest yield and revenue
- `GET /api/harvests?fieldId=1&cropId=1` — List harvest records
- `GET /api/harvests/date-range?startDate=...&endDate=...` — Date range filter
- `GET /api/harvests/report/by-crop` — **Aggregation Report**: Yield quantity & revenue grouped by crop
- `GET /api/harvests/report/farm-revenue?farmId=1` — Total harvest revenue for a farm

### 9. Comprehensive Farm Reports API (`/api/reports`)
- `GET /api/reports/financial/farm/{farmId}` — Farm profitability statement (Total Revenue - Total Expenses = Net Profit/Loss)
- `GET /api/reports/financial/all-farms` — Multi-farm financial comparison
- `GET /api/reports/field/{fieldId}/history` — Full lifecycle history (plans, activities, expenses, harvests)

---

## 🗄️ MySQL Database Setup (`agriculture_FM_System_DB`)

The project is pre-configured and connected directly to **MySQL** using:
- **Database Name**: `agriculture_FM_System_DB`
- **Port**: `3306`
- **Username**: `root`
- **Password**: `root` (or configured locally in `application.properties`)
- **Tables**: `farmers`, `farms`, `fields`, `crops`, `crop_plans`, `activities`, `expenses`, `harvests`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/agriculture_FM_System_DB?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

*(Note: An `application-h2.properties` profile is also available if you ever want to run with an in-memory database using `--spring.profiles.active=h2`)*
```powershell
cd C:\Users\reddy\Downloads\AgricultureFMSystem\AgricultureFMSystem
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

### 2. Access Swagger UI & H2 Console
- **Swagger OpenAPI Documentation**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:agriculture_db`
  - Username: `sa`
  - Password: *(leave blank)*
- **Actuator Health**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
