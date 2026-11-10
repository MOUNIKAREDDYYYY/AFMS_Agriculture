# Agriculture Farm Management System (AFMS)

A Spring Boot REST API for farmers, agronomists, and farm supervisors to manage crop planning, field tracking, daily farming activities, expenses, harvest yield records, and financial reports.

---

## Domain Model & Modules

- **User (`/api/auth`)**: User accounts and roles (`ADMIN`, `FARMER`, `AGRONOMIST`, `SUPERVISOR`) for HMAC-SHA256 JWT authentication.
- **Farmer (`/api/farmers`)**: Registered agricultural producers managing one or more farms.
- **Farm (`/api/farms`)**: Agricultural estates linked to a farmer.
- **Field (`/api/fields`)**: Individual plots within a farm with soil type, acreage, irrigation type, status (`AVAILABLE`, `PREPARATION`, `PLANTED`, `FALLOW`), and active crop.
- **Crop (`/api/crops`)**: Crop catalog entries (category, season, growth duration, expected yield, and variety).
- **CropPlan (`/api/crop-plans`)**: Seasonal crop planning cycles (`DRAFT`, `APPROVED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`) with budgets and target harvest dates.
- **Activity (`/api/activities`)**: Field operations (`PLANTING`, `IRRIGATION`, `FERTILIZING`, etc.) with schedules, assignments, and completion tracking.
- **Expense (`/api/expenses`)**: Farm/field/crop/activity expenditures across categories (`SEEDS`, `FERTILIZERS`, `PESTICIDES`, `LABOR`, `MACHINERY`, etc.).
- **Harvest (`/api/harvests`)**: Production yield records (`KG`, `QUINTAL`, `TON`, `CRATES`), quality grades, unit prices, and computed revenue.
- **Reports & Files (`/api/reports`, `/api/files`)**: Consolidated farm/farmer profitability, field lifecycle history, multipart uploads/downloads, and CSV exports.

---

## JPA Entity Relationships

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

- **Farmer → Farms**: `@OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)` with `@BatchSize(size = 20)`
- **Farm → Fields / Expenses**: `@OneToMany` with `FetchType.LAZY` and `@BatchSize(size = 20)` on `fields`
- **Field → Crop**: `@ManyToOne(fetch = FetchType.LAZY)` (`currentCrop`)
- **Field → CropPlans / Activities / Expenses / Harvests**: `@OneToMany` with `FetchType.LAZY`
- **Auditing**: `@CreatedDate` and `@LastModifiedDate` populated via `@EnableJpaAuditing` (`AuditingConfig`) and `@EntityListeners(AuditingEntityListener.class)`

---

## Package Structure

```
com.AgricultureFMSystem.AgricultureFMSystem
│
├── actuator/           # Custom Actuator endpoint (/actuator/agriculture), HealthIndicator, InfoContributor
├── aop/                # LoggingAndPerformanceAspect (@Around, @Before, @AfterThrowing)
├── config/             # OpenAPI, Auditing, Cache, Scheduler, WebMvcConfig, DataInitializer
├── controller/         # 11 REST Controllers (CRUD, business APIs, reports, auth, files)
├── dto/
│   ├── request/        # Bean Validation (@Valid) request DTOs
│   └── response/       # Typed response DTOs, financial summaries, PagedResponse<T>
├── entity/             # 10 JPA entities with lazy associations, indexes, and audit fields
├── enums/              # Domain & security enumerations (8 enums)
├── exception/          # Custom exceptions + GlobalExceptionHandler (@RestControllerAdvice)
├── mapper/             # Entity <-> DTO conversion mappers
├── repository/         # Spring Data JPA repositories, filters, and JPQL aggregations
├── scheduler/          # FarmTaskScheduler (@Scheduled overdue activity monitor)
├── security/           # JwtTokenUtil (HMAC-SHA256) & AuthSecurityInterceptor
└── service/
    └── impl/           # @Transactional service implementations & PatchHelper
```

---

## 5-Phase Topic Coverage

### Phase 1: Java Frameworks, Spring Core, Spring AOP & Lombok
- **Spring Core**: Constructor dependency injection (`@RequiredArgsConstructor`), `@PostConstruct` initialization, and externalized `@Value` properties.
- **Spring AOP**: `LoggingAndPerformanceAspect` profiles service execution latency (`@Around`), audits controller entry (`@Before`), and traces service failures (`@AfterThrowing`).
- **Lombok**: `@Getter`/`@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@RequiredArgsConstructor`, `@Slf4j`.

### Phase 2: Spring Boot, Spring MVC, REST APIs, Validation & Exceptions
- **REST & MVC**: Full CRUD (`POST`, `GET`, `GET /{id}`, `PUT`, `PATCH`, `DELETE`), pagination/sorting (`Pageable` + `PagedResponse`), CORS, and `HandlerInterceptor`.
- **Validation**: Jakarta `@Valid` on DTOs (`@NotBlank`, `@Email`, `@Pattern`, `@Size`, etc.) plus date-order checks.
- **Global Exception Handling**: `GlobalExceptionHandler` handling validation errors, `ConstraintViolationException`, `400`, `401`, `404`, `405`, `409`, and `500` uniformly.

### Phase 3: Spring Data JPA, Hibernate, Relationships & Transactions
- **Hibernate & JPA**: `FetchType.LAZY`, `JOIN FETCH` queries, `@BatchSize`, `spring.jpa.open-in-view=false`, table indexes, and unique constraints.
- **Aggregations & Filtering**: Grouped JPQL financial/yield projections, combined multi-parameter filters, date-range queries, and `@Transactional(readOnly = true)` boundaries.

### Phase 4: Swagger, Logging, File Handling, HikariCP, Scheduler, Actuator & DTO Pattern
- **Swagger / OpenAPI**: Interactive UI at `http://localhost:8080/swagger-ui.html` with `bearerAuth` JWT support (`OpenApiConfig`).
- **Logging**: SLF4J/Logback with per-profile logging levels and AOP controller/service diagnostics.
- **File Handling**: `10MB` multipart uploads, path-traversal protection, streaming downloads, and RFC-4180 CSV exports.
- **HikariCP**: Tuned `AgricultureDevHikariPool` and `AgricultureProdHikariPool` pool settings.
- **Scheduler**: Hourly `@Scheduled` background check logging overdue and due-today activities.
- **Actuator**: Built-in endpoints plus `/actuator/agriculture`, custom health indicator, and info contributor.
- **DTO Pattern**: Decoupled request/response DTOs and typed `FieldHistoryResponseDTO`.

### Phase 5: Profiles, Testing, Caching & Security (JWT)
- **Profiles**: `dev` (MySQL + demo seeder), `prod` (environment-variable MySQL + `ddl-auto=validate`), and `test` (in-memory H2).
- **Testing**: `8` JUnit 5, Mockito unit (`FarmerServiceTest`, `ExpenseServiceTest`), `@WebMvcTest` controller slice (`FarmerControllerTest`), and context-boot (`AgricultureFmSystemApplicationTests`) tests (`src/test/java`).
- **Caching**: `@EnableCaching` with `ConcurrentMapCacheManager` (`crops`, `farmers`, `farms`, `fieldHistory`) and `@CacheEvict` on mutations.
- **Security (JWT)**: PBKDF2 password hashing, HMAC-SHA256 signed JWTs, and Bearer token enforcement (`app.security.require-auth=true`) across `/api/**`.

---

## API Endpoints Overview

> **Authentication note**: Protected `/api/**` routes require `Authorization: Bearer <token>`. Call `POST /api/auth/login` (seeded dev credentials: `admin` / `admin123` or `farmer_rajesh` / `farmer123`) first and authorize in Swagger UI or Postman.

### 0. Authentication (`/api/auth`)
- `POST /api/auth/login` — Authenticate and receive a Bearer JWT
- `POST /api/auth/register` — Register a new user
- `GET /api/auth/me` — Get current authenticated user profile

### 1. Farmer API (`/api/farmers`)
- `POST /api/farmers` · `GET /api/farmers` · `GET /api/farmers/{id}` · `GET /api/farmers/search?query=patel`
- `PUT /api/farmers/{id}` · `PATCH /api/farmers/{id}` · `PATCH /api/farmers/{id}/status` · `DELETE /api/farmers/{id}`

### 2. Farm API (`/api/farms`)
- `POST /api/farms` · `GET /api/farms?farmerId=1` · `GET /api/farms/{id}` · `GET /api/farms/search?query=organic`
- `PUT /api/farms/{id}` · `PATCH /api/farms/{id}` · `DELETE /api/farms/{id}`

### 3. Field API (`/api/fields`)
- `POST /api/fields` · `GET /api/fields?farmId=1&status=AVAILABLE` · `GET /api/fields/{id}` · `GET /api/fields/search?query=alluvial`
- `PATCH /api/fields/{id}/status?status=PLANTED` · `POST /api/fields/{id}/assign-crop?cropId=1` · `PUT` / `PATCH` / `DELETE`

### 4. Crop API (`/api/crops`)
- `POST /api/crops` · `GET /api/crops?category=GRAIN&season=RABI` · `GET /api/crops/{id}` · `GET /api/crops/search?query=wheat`
- `PUT` / `PATCH` / `DELETE`

### 5. Crop Planning API (`/api/crop-plans`)
- `POST /api/crop-plans` · `GET /api/crop-plans?fieldId=1&status=IN_PROGRESS&season=RABI` · `GET /api/crop-plans/search?query=wheat`
- `GET /api/crop-plans/date-range?startDate=2026-01-01&endDate=2026-12-31`
- `POST /api/crop-plans/{id}/approve` · `PATCH /api/crop-plans/{id}/status` · `POST /api/crop-plans/{id}/cancel` · `PUT` / `PATCH` / `DELETE`

### 6. Activity Tracking API (`/api/activities`)
- `POST /api/activities` · `GET /api/activities?fieldId=1&status=PLANNED&type=IRRIGATION` · `GET /api/activities/search?query=drip`
- `GET /api/activities/pending` · `GET /api/activities/date-range?startDate=...&endDate=...`
- `POST /api/activities/{id}/complete` · `PATCH /api/activities/{id}/status` · `POST /api/activities/{id}/cancel` · `PUT` / `PATCH` / `DELETE`

### 7. Expense Reports API (`/api/expenses`)
- `POST /api/expenses` · `GET /api/expenses?farmId=1&fieldId=1&category=FERTILIZERS` · `GET /api/expenses/search?query=seeds`
- `GET /api/expenses/date-range?startDate=...&endDate=...`
- `GET /api/expenses/report/by-category` · `GET /api/expenses/report/filtered?farmId=1` · `GET /api/expenses/report/farm-total?farmId=1` · `PUT` / `PATCH` / `DELETE`

### 8. Harvest Records API (`/api/harvests`)
- `POST /api/harvests` · `GET /api/harvests?fieldId=1&cropId=1` · `GET /api/harvests/search?query=Grade`
- `GET /api/harvests/date-range?startDate=...&endDate=...`
- `GET /api/harvests/report/by-crop` · `GET /api/harvests/report/by-crop-and-date` · `GET /api/harvests/report/farm-revenue?farmId=1` · `PUT` / `PATCH` / `DELETE`

### 9. Comprehensive Reports API (`/api/reports`)
- `GET /api/reports/financial/farm/{farmId}` · `GET /api/reports/financial/all-farms`
- `GET /api/reports/financial/farmer/{farmerId}` · `GET /api/reports/financial/all-farmers`
- `GET /api/reports/field/{fieldId}/history`

### 10. File Handling & CSV Exports (`/api/files`)
- `POST /api/files/upload` (`multipart/form-data`) · `GET /api/files/download/{fileName}`
- `GET /api/files/export/harvests-csv` · `GET /api/files/export/expenses-csv?farmId=1`

---

## Database & Running Locally

- **Active Dev Profile**: `dev` (configured in `src/main/resources/application.properties` on port `8080`)
- **MySQL Database**: `agriculture_FM_System_DB` (`localhost:3306`, Hibernate `ddl-auto=update`)
- **Tables**: `users`, `farmers`, `farms`, `fields`, `crops`, `crop_plans`, `activities`, `expenses`, `harvests`
- **Production Profile (`prod`)**: Configured via `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, and `ddl-auto=validate`
- **Test Profile (`test`)**: Isolated in-memory H2 database (`jdbc:h2:mem:agriculture_test_db`)

```powershell
cd C:\Users\reddy\Downloads\AgricultureFMSystem
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

### Useful Local URLs (Port `8080`)
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **Actuator Health**: `http://localhost:8080/actuator/health`
- **Custom Agricultural Endpoint**: `http://localhost:8080/actuator/agriculture`
