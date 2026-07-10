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

    if (parsed.errors.length > 0) {
      fs.unlinkSync(req.file.path);

      res.status(400).json({
        success: false,
        message: "Invalid CSV format.",
        errors: parsed.errors,
      });
      return;
    }

    const rows = parsed.data;

    // ==========================
    // Process with AI
    // ==========================
    const result = await processCsv(rows);

    // ==========================
    // Delete Uploaded File
    // ==========================
    fs.unlinkSync(req.file.path);

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

    // Cleanup uploaded file if something failed
    if (req.file && fs.existsSync(req.file.path)) {
      fs.unlinkSync(req.file.path);
    }

    res.status(500).json({
      success: false,
      message: "Failed to process CSV.",
    });
  }
}