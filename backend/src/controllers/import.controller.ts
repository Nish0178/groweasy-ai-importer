import fs from "node:fs";
import { Request, Response } from "express";
import Papa from "papaparse";

import { processCsv } from "../services/csv.service";
import { generateExcel } from "../services/excel.service";

export async function uploadCsvController(
  req: Request,
  res: Response
): Promise<void> {
  try {
    // ======================================
    // Validate Upload
    // ======================================

    console.log("Upload Request:", {
      file: req.file?.originalname,
      mimetype: req.file?.mimetype,
      size: req.file?.size,
    });

    if (!req.file) {
      res.status(400).json({
        success: false,
        message: "CSV file is required.",
      });
      return;
    }

    // ======================================
    // Read CSV
    // ======================================

    const csvFile = fs.readFileSync(req.file.path, "utf8");

    // ======================================
    // Parse CSV
    // ======================================

    const parsed = Papa.parse(csvFile, {
      header: true,
      skipEmptyLines: true,
    });

   const fatalErrors = parsed.errors.filter(
  (error: Papa.ParseError) => error.code !== "UndetectableDelimiter"
);
    if (
      fatalErrors.length > 0 &&
      (!parsed.data || parsed.data.length === 0)
    ) {
      res.status(400).json({
        success: false,
        message: "Invalid CSV format.",
        errors: fatalErrors,
      });
      return;
    }

    const rows = parsed.data as Record<string, any>[];

    // ======================================
    // AI Processing
    // ======================================

    const result = await processCsv(rows);

    // ======================================
    // Generate Excel
    // ======================================

    const excelFile = await generateExcel(result.records);

    console.log("Excel Generated:", excelFile);

    // ======================================
    // Success Response
    // ======================================

    res.status(200).json({
      success: true,
      totalImported: result.records.length,
      totalSkipped: result.skipped,
      records: result.records,
      downloadUrl: `/exports/${excelFile}`,
    });
  } catch (error) {
    console.error("Upload Controller Error:", error);

    res.status(500).json({
      success: false,
      message:
        error instanceof Error
          ? error.message
          : "Failed to process CSV.",
    });
  } finally {
    // ======================================
    // Delete Uploaded File
    // ======================================

    if (req.file && fs.existsSync(req.file.path)) {
      try {
        fs.unlinkSync(req.file.path);
      } catch (error) {
        console.error("Failed to delete uploaded file:", error);
      }
    }
  }
}