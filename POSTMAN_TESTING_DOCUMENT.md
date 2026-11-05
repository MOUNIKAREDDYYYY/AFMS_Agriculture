# Agriculture Farm Management System (AFMS) - Postman Testing Document

This document provides testing specifications for all REST endpoints in the Agriculture Farm Management System.
The accompanying file **`Agriculture_Farm_Management_System.postman_collection.json`** can be directly imported into Postman and contains **100% clean, standard JSON payloads with no comments** for error-free execution.

---

## Postman Configuration

- **Collection Variable**: `baseUrl` = `http://localhost:8080`
- **Request Header**: `Content-Type: application/json` (for all POST / PUT / PATCH requests)
- **Pre-seeded Login Credentials**:
  - **Admin**: `admin` / `admin123`
  - **Farmer**: `farmer_rajesh` / `farmer123`

---

## Endpoint Testing Specifications

---

### 0. Authentication & Security (`/api/auth`)

#### 0.1 Login User
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/auth/login`
- **Expected Status**: `200 OK`

**Request Body:**
```json
{
  "usernameOrEmail": "admin",
  "password": "admin123"
}
```

**Response Body:**
```json
{
  "token": "afms_jwt_550e8400e29b41d4a716446655440000_YWRtaW4=",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "admin",
  "email": "admin@agriculturefms.com",
  "fullName": "System Administrator",
  "role": "ADMIN",
  "message": "Login successful"
}
```

#### 0.2 Register New User
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/auth/register`
- **Expected Status**: `201 Created`

**Request Body:**
```json
{
  "username": "farmer_anita",
  "email": "anita.sharma@greenfields.org",
  "password": "anita123",
  "fullName": "Anita Ramesh Sharma",
  "role": "FARMER"
}
```

#### 0.3 Get Current User Profile
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/auth/me?username=admin`
- **Expected Status**: `200 OK`

---

### 1. Farmer Management (`/api/farmers`)

#### 1.1 Create Farmer
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/farmers`
- **Expected Status**: `201 Created`

**Request Body:**
```json
{
  "fullName": "Vikram Devendra Patil",
  "email": "vikram.patil@kisanagro.com",
  "phoneNumber": "+91-9876501234",
  "address": "Plot 14, Agri Cluster, Nashik, Maharashtra",
  "governmentIdNumber": "AGRI-MH-2026-9011",
  "active": true
}
```

#### 1.2 List All Farmers (Paginated & Sorted)
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/farmers?page=0&size=10&sort=createdAt,desc`
- **Expected Status**: `200 OK`

#### 1.3 Filter Farmers by Active Status
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/farmers?active=true&page=0&size=10`
- **Expected Status**: `200 OK`

#### 1.4 Get Farmer by ID
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/farmers/1`
- **Expected Status**: `200 OK`

#### 1.5 Search Farmers
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/farmers/search?query=Patel&page=0&size=10`
- **Expected Status**: `200 OK`

#### 1.6 Full Update Farmer
- **Method**: `PUT`
- **URL**: `{{baseUrl}}/api/farmers/1`
- **Expected Status**: `200 OK`

**Request Body:**
```json
{
  "fullName": "Vikram D. Patil",
  "email": "vikram.patil@kisanagro.com",
  "phoneNumber": "+91-9876509999",
  "address": "Plot 14, Agri Cluster, Nashik, Maharashtra - 422003",
  "governmentIdNumber": "AGRI-MH-2026-9011",
  "active": true
}
```

#### 1.7 Partial Update Farmer
- **Method**: `PATCH`
- **URL**: `{{baseUrl}}/api/farmers/1`
- **Expected Status**: `200 OK`

**Request Body:**
```json
{
  "phoneNumber": "+91-9123456789"
}
```

#### 1.8 Toggle Farmer Active Status
- **Method**: `PATCH`
- **URL**: `{{baseUrl}}/api/farmers/1/status`
- **Expected Status**: `200 OK`

#### 1.9 Delete Farmer
- **Method**: `DELETE`
- **URL**: `{{baseUrl}}/api/farmers/99`
- **Expected Status**: `204 No Content`

---

### 2. Farm Management (`/api/farms`)

#### 2.1 Create Farm
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/farms`
- **Expected Status**: `201 Created`

**Request Body:**
```json
{
  "name": "Sahyadri Organic Plantation",
  "location": "Trimbakeshwar Road, Nashik",
  "totalAreaInAcres": 65.5,
  "farmerId": 1
}
```

#### 2.2 Filter Farms by Farmer ID
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/farms?farmerId=1&page=0&size=10`
- **Expected Status**: `200 OK`

#### 2.3 Search Farms
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/farms/search?query=Organic&page=0&size=10`
- **Expected Status**: `200 OK`

---

### 3. Field Management (`/api/fields`)

#### 3.1 Create Field Plot
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/fields`
- **Expected Status**: `201 Created`

**Request Body:**
```json
{
  "name": "Valley Ridge Sector D",
  "areaInAcres": 12.5,
  "soilType": "Black Loamy Clay",
  "irrigationType": "Automated Drip Irrigation",
  "status": "AVAILABLE",
  "farmId": 1,
  "currentCropId": null
}
```

#### 3.2 List Fields (Filtered by Farm & Status)
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/fields?farmId=1&status=PLANTED&page=0&size=10`
- **Expected Status**: `200 OK`

#### 3.3 Update Field Status
- **Method**: `PATCH`
- **URL**: `{{baseUrl}}/api/fields/1/status?status=PREPARATION`
- **Expected Status**: `200 OK`

#### 3.4 Assign Crop to Field
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/fields/1/assign-crop?cropId=1`
- **Expected Status**: `200 OK`

---

### 4. Crop Management (`/api/crops`)

#### 4.1 Register New Crop
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/crops`
- **Expected Status**: `201 Created`

**Request Body:**
```json
{
  "name": "Mustard",
  "variety": "Pusa Bold Hybrid",
  "category": "OILSEED",
  "season": "RABI",
  "growthDurationDays": 110,
  "optimalTemperature": "12C - 25C",
  "waterRequirement": "Low (250-350 mm)",
  "standardYieldPerAcre": 9.5,
  "yieldUnit": "QUINTAL"
}
```

#### 4.2 List Crops Filtered by Category & Season
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/crops?category=GRAIN&season=RABI&page=0&size=10`
- **Expected Status**: `200 OK`

---

### 5. Crop Planning API (`/api/crop-plans`)

#### 5.1 Create Crop Plan
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/crop-plans`
- **Expected Status**: `201 Created`

**Request Body:**
```json
{
  "fieldId": 1,
  "cropId": 1,
  "season": "RABI",
  "plannedStartDate": "2026-11-15",
  "estimatedHarvestDate": "2027-03-25",
  "expectedYieldQuantity": 280.0,
  "yieldUnit": "QUINTAL",
  "estimatedBudget": 125000.00,
  "status": "DRAFT",
  "notes": "Comprehensive Rabi wheat planning with certified high-yield seeds."
}
```

#### 5.2 Filter Crop Plans by Date Range
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/crop-plans/date-range?startDate=2026-01-01&endDate=2026-12-31&page=0&size=10`
- **Expected Status**: `200 OK`

#### 5.3 Approve Crop Plan (`/approve`)
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/crop-plans/1/approve`
- **Expected Status**: `200 OK`

#### 5.4 Cancel Crop Plan (`/cancel`)
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/crop-plans/1/cancel`
- **Expected Status**: `200 OK`

---

### 6. Activity Tracking API (`/api/activities`)

#### 6.1 Schedule Farming Activity
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/activities`
- **Expected Status**: `201 Created`

**Request Body:**
```json
{
  "title": "Second Dose Urea Top Dressing",
  "activityType": "FERTILIZING",
  "status": "PLANNED",
  "scheduledDate": "2026-11-20",
  "assignedTo": "Gopal Verma",
  "notes": "Apply 45kg per acre followed by immediate light irrigation",
  "fieldId": 1,
  "cropId": 1
}
```

#### 6.2 Get Pending Activities
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/activities/pending`
- **Expected Status**: `200 OK`

#### 6.3 Filter Activities by Scheduled Date Range
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/activities/date-range?startDate=2026-11-01&endDate=2026-11-30&page=0&size=10`
- **Expected Status**: `200 OK`

#### 6.4 Complete Activity (`/complete`)
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/activities/1/complete?completionDate=2026-11-05&notes=Completed+successfully+on+time`
- **Expected Status**: `200 OK`

---

### 7. Expense Reports API (`/api/expenses`)

#### 7.1 Log Farm Expense
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/expenses`
- **Expected Status**: `201 Created`

**Request Body:**
```json
{
  "title": "Drip Irrigation Pipe Fittings & Filters",
  "category": "IRRIGATION",
  "amount": 8450.00,
  "expenseDate": "2026-11-05",
  "paymentMethod": "UPI",
  "vendorName": "Jain Irrigation Agro Store",
  "notes": "Replacement of clogged inline micro-drippers",
  "farmId": 1,
  "fieldId": 1,
  "cropId": 1,
  "activityId": null
}
```

#### 7.2 Expense Aggregation Report by Category
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/expenses/report/by-category`
- **Expected Status**: `200 OK`

#### 7.3 Filtered Expense Report (Farm + Date Range Aggregation)
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/expenses/report/filtered?farmId=1&startDate=2026-01-01&endDate=2026-12-31`
- **Expected Status**: `200 OK`

#### 7.4 Total Farm Expenditure Sum
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/expenses/report/farm-total?farmId=1`
- **Expected Status**: `200 OK`

---

### 8. Harvest Records API (`/api/harvests`)

#### 8.1 Record Harvest Yield
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/harvests`
- **Expected Status**: `201 Created`

**Request Body:**
```json
{
  "harvestDate": "2026-11-05",
  "quantity": 120.0,
  "unit": "QUINTAL",
  "qualityGrade": "Export Quality A",
  "unitPrice": 3400.00,
  "totalRevenue": 408000.00,
  "storageLocation": "Silo 4, Agro Terminal",
  "notes": "Winter wheat harvest with high grain test weight.",
  "fieldId": 1,
  "cropId": 1
}
```

#### 8.2 Harvest Yield & Revenue Report by Crop
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/harvests/report/by-crop`
- **Expected Status**: `200 OK`

#### 8.3 Total Harvest Revenue for a Farm
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/harvests/report/farm-revenue?farmId=1`
- **Expected Status**: `200 OK`

---

### 9. Comprehensive Farm Reports (`/api/reports`)

#### 9.1 Farm Financial Profitability Statement (`/report`)
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/reports/financial/farm/1`
- **Expected Status**: `200 OK`

#### 9.2 All Farms Financial Comparison
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/reports/financial/all-farms`
- **Expected Status**: `200 OK`

#### 9.3 Farmer Consolidated Financial Statement (All farms of a farmer)
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/reports/financial/farmer/1` (Rajesh Kumar Patel)
- **URL (Farmer 2)**: `{{baseUrl}}/api/reports/financial/farmer/2` (Anita Ramesh Sharma)
- **Expected Status**: `200 OK`

#### 9.4 All Farmers Consolidated Financial Statement
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/reports/financial/all-farmers`
- **Expected Status**: `200 OK`

#### 9.5 Field Complete Lifecycle History (`/history`)
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/reports/field/1/history`
- **Expected Status**: `200 OK`

---

### 10. Spring Boot Actuator & System Monitoring (`/actuator`)

#### 10.1 Actuator Discovery Links
- **Method**: `GET`
- **URL**: `{{baseUrl}}/actuator`
- **Expected Status**: `200 OK`

#### 10.2 System & Agricultural Ecosystem Health
- **Method**: `GET`
- **URL**: `{{baseUrl}}/actuator/health`
- **Expected Status**: `200 OK`
- **Response Features**: Reports overall `UP` status, MySQL connectivity, disk space, and custom `agricultural` health details (`totalFarmers`, `totalFarms`, `totalFields`, `totalCropsRegistered`, `systemReadiness`).

#### 10.3 Application & Agronomic Telemetry Info
- **Method**: `GET`
- **URL**: `{{baseUrl}}/actuator/info`
- **Expected Status**: `200 OK`
- **Response Features**: Java 22 runtime info, Windows OS architecture, and dynamic `agriculture-system` statistics (`registeredFarmers`, `activeFarms`, `registeredCrops`, `totalFarmActivities`, `totalHarvestEvents`, `totalSystemHarvestRevenue`).

#### 10.4 Custom Agricultural Operations Snapshot (`@Endpoint`)
- **Method**: `GET`
- **URL**: `{{baseUrl}}/actuator/agriculture`
- **Expected Status**: `200 OK`
- **Response Features**: Real-time snapshot of agricultural operations, active crop plans, total harvest revenue across all farms, database status, and hourly scheduler execution.

#### 10.5 Scheduled Tasks Monitor
- **Method**: `GET`
- **URL**: `{{baseUrl}}/actuator/scheduledtasks`
- **Expected Status**: `200 OK`
- **Response Features**: Live status, interval (`3600000ms`), last run, and next scheduled run of `FarmTaskScheduler.monitorOverdueActivities`.

#### 10.6 Metrics Index
- **Method**: `GET`
- **URL**: `{{baseUrl}}/actuator/metrics`
- **Expected Status**: `200 OK`

#### 10.7 JVM Memory Metric
- **Method**: `GET`
- **URL**: `{{baseUrl}}/actuator/metrics/jvm.memory.used`
- **Expected Status**: `200 OK`

#### 10.8 Application HTTP Request URL Mappings
- **Method**: `GET`
- **URL**: `{{baseUrl}}/actuator/mappings`
- **Expected Status**: `200 OK`

---

### 11. File Handling & CSV Exports (`/api/files`)

#### 11.1 Export Harvest Records to CSV
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/files/export/harvests-csv`
- **Expected Status**: `200 OK`
- **Response**: Streams a downloadable `harvest_records_export.csv` containing all harvests across all farms.

#### 11.2 Export Farm Expenditures to CSV
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/files/export/expenses-csv?farmId=1`
- **Expected Status**: `200 OK`
- **Response**: Streams a downloadable `expenses_farm_1.csv` containing all expenditure records.

#### 11.3 Upload Agricultural Document (Soil Test, Receipt, Photo)
- **Method**: `POST`
- **URL**: `{{baseUrl}}/api/files/upload`
- **Body Type**: `form-data`
- **Key**: `file` (File)
- **Expected Status**: `201 Created`
- **Response Structure**:
```json
{
  "fileName": "5afb1c0b_sample_soil_report.txt",
  "originalFileName": "sample_soil_report.txt",
  "fileDownloadUri": "http://localhost:8080/api/files/download/5afb1c0b_sample_soil_report.txt",
  "fileType": "text/plain",
  "size": 131,
  "uploadTimestamp": "2026-11-05T14:18:45.3287621",
  "message": "File uploaded successfully"
}
```

#### 11.4 Download / Stream Stored Document
- **Method**: `GET`
- **URL**: `{{baseUrl}}/api/files/download/{fileName}`
- **Expected Status**: `200 OK`
- **Response**: Direct binary stream of the requested file with `Content-Disposition: attachment`.

---

## How to Import into Postman

1. Open **Postman**.
2. Click **Import** (top left).
3. Drag & drop or select:
   `C:\Users\reddy\Downloads\AgricultureFMSystem\Agriculture_Farm_Management_System.postman_collection.json`
4. All requests are ready to run directly with valid JSON bodies.
