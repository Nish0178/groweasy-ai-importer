import { Request, Response } from "express";
import Papa from "papaparse";

import { processCsv } from "../services/csv.service";
import { generateExcel } from "../services/excel.service";

export async function uploadCsvController(
  req: Request,
  res: Response
): Promise<void> {
  try {
    if (!req.file) {
      res.status(400).json({
        success: false,
        message: "CSV file is required.",
      });
      return;
    }

    // Read CSV directly from memory buffer
    const csvContent = req.file.buffer.toString("utf8");

    if (!csvContent.trim()) {
      res.status(400).json({
        success: false,
        message: "The uploaded CSV file is empty.",
      });
      return;
    }

    // Parse CSV
    const parsed = Papa.parse<Record<string, any>>(csvContent, {
      header: true,
      skipEmptyLines: true,
    });

    const fatalErrors = parsed.errors.filter(
      (error: Papa.ParseError) => error.code !== "UndetectableDelimiter"
    );

    if (fatalErrors.length > 0 && (!parsed.data || parsed.data.length === 0)) {
      res.status(400).json({
        success: false,
        message: "Invalid CSV format.",
        errors: fatalErrors,
      });
      return;
    }

    const rows = parsed.data;

    if (rows.length === 0) {
      res.status(400).json({
        success: false,
        message: "No data rows found in CSV file.",
      });
      return;
    }

    // AI Processing
    const result = await processCsv(rows);

    // Generate Excel File
    const excelFile = await generateExcel(result.records);

    // Return Success Response
    res.status(200).json({
      success: true,
      totalImported: result.records.length,
      totalSkipped: result.skipped,
      records: result.records,
      downloadUrl: `/exports/${excelFile}`,
    });
  } catch (error) {
    const message =
      error instanceof Error ? error.message : "Failed to process CSV.";

    res.status(500).json({
      success: false,
      message,
    });
  }
}