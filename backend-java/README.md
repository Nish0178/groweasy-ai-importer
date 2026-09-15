# GrowEasy AI Importer - Java Spring Boot Backend

A high-performance standalone Java (Spring Boot 3 + Java 21) backend for the **GrowEasy AI Importer**.
This service faithfully mirrors the existing Node.js backend endpoints, allowing the Next.js frontend to communicate with either backend seamlessly.

---

## 🚀 Features

- **Standard REST API**:
  - `GET /api/health`: Health status & Java runtime info
  - `POST /api/import/upload`: Multipart CSV upload & AI extraction
  - `GET /exports/{filename}`: Static download of generated `.xlsx` spreadsheets
- **Apache Commons CSV**: Robust CSV parsing with automatic delimiter detection (`,`, `;`, `\t`) and header mapping.
- **Google Gemini AI Integration**: Direct HTTP integration with `gemini-2.5-flash` using `CRM_EXTRACTION_PROMPT` to normalize raw spreadsheet rows into standard GrowEasy CRM records in batches of 20.
- **Apache POI Excel Engine**: Generates `.xlsx` reports with formatted headers, cell styles, and auto-adjusted column widths.
- **CORS Configured**: Pre-configured to allow local frontend (`http://localhost:3000`) and deployed web applications.

---

## 🛠️ Requirements

- **Java Development Kit (JDK)**: Java 21 or higher
- **Maven**: Version 3.8+ (or use an IDE like IntelliJ IDEA / Eclipse / VS Code)

---

## ⚙️ Configuration & Environment Variables

Create or update `.env` or set environment variables before running:

| Variable | Description | Default |
|---|---|---|
| `PORT` | Server listening port | `5000` |
| `GEMINI_API_KEY` | Google Gemini API Key | Pre-configured from environment |
| `GEMINI_MODEL` | Gemini Model identifier | `gemini-2.5-flash` |

---

## 🏃 Running the Application

### Option 1: Using Maven Command Line
```bash
cd backend-java
mvn clean spring-boot:run
```

### Option 2: Build Executable JAR
```bash
cd backend-java
mvn clean package -DskipTests
java -jar target/groweasy-ai-importer-1.0.0.jar
```

### Option 3: Run via IDE
Open `backend-java` in IntelliJ IDEA, Eclipse, or VS Code, and run `GrowEasyImporterApplication.java`.

---

## 📡 API Reference

### 1. Health Check
```http
GET /api/health
```
**Response (200 OK):**
```json
{
  "success": true,
  "message": "GrowEasy AI Importer Java Backend is running 🚀",
  "runtime": "Java 21.0.11",
  "timestamp": "2026-09-15T10:00:00Z"
}
```

### 2. Upload CSV
```http
POST /api/import/upload
Content-Type: multipart/form-data
```
Body:
`file`: `<your-leads.csv>`

**Response (200 OK):**
```json
{
  "success": true,
  "totalImported": 12,
  "totalSkipped": 1,
  "downloadUrl": "/exports/crm_records_1726394800000.xlsx",
  "records": [
    {
      "name": "Sarah Connor",
      "email": "sarah@cyberdyne.com",
      "country_code": "+1",
      "mobile_without_country_code": "5550199",
      "company": "Cyberdyne Systems",
      "crm_status": "GOOD_LEAD_FOLLOW_UP"
    }
  ]
}
```

### 3. Download Excel File
```http
GET /exports/crm_records_1726394800000.xlsx
```
Streams the generated spreadsheet directly to the browser.
