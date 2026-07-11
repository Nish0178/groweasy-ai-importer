import fs from "node:fs";
import { Request, Response } from "express";
import Papa from "papaparse";

import { processCsv } from "../services/csv.service";

export async function uploadCsvController(
  req: Request,
  res: Response
): Promise<void> {
  try {
    // ==========================
    // Validate File
    // ==========================
    console.log("Upload request received", {
      file: req.file?.originalname,
      mimetype: req.file?.mimetype,
      size: req.file?.size,
    });

    if (!req.file) {
      console.log("Upload failed: no file received", {
        body: req.body,
        headers: req.headers,
      });

      res.status(400).json({
        success: false,
        message: "CSV file is required.",
      });
      return;
    }

    // ==========================
    // Read CSV
    // ==========================
    const csvFile = fs.readFileSync(req.file.path, "utf8");

    // ==========================
    // Parse CSV
    // ==========================
    const parsed = Papa.parse(csvFile, {
      header: true,
      skipEmptyLines: true,
    });

    const fatalErrors = parsed.errors.filter(
      (e) => e.code !== "UndetectableDelimiter"
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

    // ==========================
    // Process with AI
    // ==========================
    const result = await processCsv(rows);

    // ==========================
    // Response
    // ==========================
    res.status(200).json({
      success: true,
      totalImported: result.records.length,
      totalSkipped: result.skipped,
      records: result.records,
    });
  } catch (error) {
    console.error("Upload Controller Error:", error);

    const errorMessage =
      error instanceof Error ? error.message : "Failed to process CSV.";

    res.status(500).json({
      success: false,
      message: errorMessage,
    });
  } finally {
    // ==========================
    // Delete Uploaded File
    // ==========================
    if (req.file && fs.existsSync(req.file.path)) {
      try {
        fs.unlinkSync(req.file.path);
      } catch (err) {
        console.error("Failed to delete temp file:", err);
      }
    }
  }
}