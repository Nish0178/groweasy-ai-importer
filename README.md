# 🚀 GrowEasy AI Importer

<div align="center">

![GrowEasy AI Importer Icon](frontend/public/icon.png)

**Intelligent AI-Powered CRM Lead Normalization & Field Mapping**

Turn messy spreadsheets, Facebook Lead Ads exports, and legacy CRM CSVs into clean, normalized GrowEasy CRM pipeline records in seconds.

[![Next.js](https://img.shields.io/badge/Next.js-16-black?style=flat&logo=next.js)](https://nextjs.org/)
[![React](https://img.shields.io/badge/React-19-61dafb?style=flat&logo=react)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue?style=flat&logo=typescript)](https://www.typescriptlang.org/)
[![Node.js](https://img.shields.io/badge/Node.js-Express-green?style=flat&logo=node.js)](https://nodejs.org/)
[![Java](https://img.shields.io/badge/Java-21%20Spring%20Boot%203-red?style=flat&logo=openjdk)](https://spring.io/projects/spring-boot)
[![Gemini AI](https://img.shields.io/badge/Google%20Gemini-2.5%20Flash-orange?style=flat&logo=google)](https://ai.google.dev/)

</div>

---

## 📖 Overview

Real-world lead data is messy: inconsistent headers (`ph_no`, `Mobile`, `Contact Number`, `Phone`), international numbers without proper country codes, concatenated full names, missing companies, and chaotic notes.

**GrowEasy AI Importer** eliminates manual spreadsheet cleaning by combining:
1. **Intelligent CSV Ingestion**: Automatic delimiter detection (`,`, `;`, `\t`) and header extraction.
2. **Gemini AI Extraction Engine**: Batched processing via Google Gemini AI (`gemini-2.5-flash`) that extracts clean lead entities and maps CRM status.
3. **Dual Backend Support**: Run with either the **Node.js/TypeScript (Express)** backend or the high-performance **Java 21 (Spring Boot 3)** backend.
4. **Instant Excel Export**: Generates auto-styled `.xlsx` spreadsheets ready for direct CRM pipeline import.
5. **Modern Interactive UI**: Next.js 16 + React 19 frontend with drag-and-drop file upload, live progress tracking, interactive lead data preview, and instant download.

---

## 🏗️ Architecture & Project Structure

```text
groweasy-ai-importer/
├── frontend/             # Next.js 16 (App Router), React 19, Tailwind CSS v4
│   ├── app/              # Routes, layout, icons (icon.png, favicon.ico)
│   ├── components/       # ThemeToggle, Navbar, Uploader, Lead Preview table
│   ├── services/         # API client & backend connection
│   └── public/           # Static assets, brand icons, logo
│
├── backend/              # Node.js + Express + TypeScript Backend
│   ├── src/
│   │   ├── controllers/  # Upload and import handlers
│   │   ├── services/     # Gemini AI & Excel generation services
│   │   ├── routes/       # REST API routes
│   │   ├── prompts/      # CRM prompt templates
│   │   └── server.ts     # Express server entry point
│   └── uploads/ & exports/
│
└── backend-java/         # Standalone Java 21 + Spring Boot 3 Backend
    ├── src/main/java/    # Spring Boot controllers, services, config
    │   └── com/groweasy/importer/
    │       ├── controller/   # ImportController & HealthController
    │       ├── service/      # GeminiAiService, CsvParserService, ExcelExportService
    │       └── model/        # CrmRecord DTOs
    ├── pom.xml           # Maven configuration (Java 21, Spring Boot 3.3)
    └── run.ps1           # PowerShell automated launcher script
```

---

## ✨ Features

- 📂 **Universal CSV Upload**: Drag-and-drop or select any CSV file (supports comma, semicolon, and tab-delimited files).
- 🤖 **AI Field Mapping**: Uses Google Gemini to detect name variations, split country code and mobile numbers, extract emails, and infer company names.
- 🏷️ **Smart CRM Lead Status Categorization**: Classifies leads into:
  - `GOOD_LEAD_FOLLOW_UP`
  - `NEEDS_MORE_INFO`
  - `UNQUALIFIED`
  - `DUPLICATE_OR_INVALID`
- 📊 **Formatted `.xlsx` Export**: Styled headers, proper column auto-sizing, and direct download links.
- ⚡ **Dual-Engine Flexibility**: Use Node.js for rapid TS development or Java Spring Boot for enterprise workloads.
- 🌓 **Dark / Light Mode**: Sleek theme support with seamless transitions.

---

## ⚙️ Environment Variables

### Backend (`backend/.env` or `backend-java/.env`)

```env
PORT=5000
GEMINI_API_KEY=your_google_gemini_api_key_here
GEMINI_MODEL=gemini-2.5-flash
FRONTEND_URL=http://localhost:3000
```

### Frontend (`frontend/.env.local`)

```env
NEXT_PUBLIC_API_URL=http://localhost:5000
```

---

## 🚀 Quick Start Guide

### 1. Prerequisites
- **Node.js**: v18 or higher (v20+ recommended)
- **npm** or **pnpm**
- *(Optional for Java backend)*: **Java 21+** and **Maven 3.8+**

---

### 2. Running the Frontend (Next.js)

```bash
cd frontend
npm install
npm run dev
```

The frontend will start at **`http://localhost:3000`**.

---

### 3. Running the Backend

You can run **either** the Node.js backend or the Java Spring Boot backend on port `5000`:

#### Option A: Node.js (TypeScript) Backend
```bash
cd backend
npm install
npm run dev
```
Server runs at **`http://localhost:5000`**.

#### Option B: Java (Spring Boot 3) Backend
```bash
cd backend-java

# Using Maven:
mvn clean spring-boot:run

# Or run the included PowerShell script:
./run.ps1
```
Server runs at **`http://localhost:5000`**.

---

## 📡 REST API Endpoints

Both backends expose the identical REST API contract:

### 1. Health Check
```http
GET /api/health
```
**Response (200 OK):**
```json
{
  "success": true,
  "message": "GrowEasy AI Importer Backend is running 🚀",
  "runtime": "Node.js / Java 21",
  "timestamp": "2026-09-15T10:00:00Z"
}
```

### 2. Upload and Process CSV
```http
POST /api/import/upload
Content-Type: multipart/form-data
```
**Form Data:**
- `file`: `<your_leads.csv>`

**Response (200 OK):**
```json
{
  "success": true,
  "totalImported": 25,
  "totalSkipped": 2,
  "downloadUrl": "/exports/crm_records_1726394800000.xlsx",
  "records": [
    {
      "name": "Arjun Sharma",
      "email": "arjun.sharma@techcorp.com",
      "country_code": "+91",
      "mobile_without_country_code": "9876543210",
      "company": "TechCorp India",
      "crm_status": "GOOD_LEAD_FOLLOW_UP",
      "notes": "Interested in commercial real estate plots"
    }
  ]
}
```

### 3. Download Generated Excel Spreadsheet
```http
GET /exports/{filename}
```
Downloads the processed `.xlsx` file.

---

## 🛡️ Target CRM Schema

| Field | Description | Example |
|---|---|---|
| `name` | Clean full name of the lead | `Arjun Sharma` |
| `email` | Validated lead email address | `arjun.sharma@techcorp.com` |
| `country_code` | International dialing code | `+91` |
| `mobile_without_country_code` | Pure national phone number digits | `9876543210` |
| `company` | Organization or business name | `TechCorp India` |
| `crm_status` | Actionable pipeline status | `GOOD_LEAD_FOLLOW_UP` |
| `notes` | Extracted intent or requirements | `Interested in commercial plots` |

---

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

Distributed under the ISC License. See `LICENSE` for more information.
