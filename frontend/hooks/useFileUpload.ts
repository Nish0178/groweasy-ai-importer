"use client";

import { useState } from "react";
import { uploadCsv } from "@/services/upload.service";
import type { UploadedFile } from "@/types/upload";
import type { CsvRow } from "@/types/csv";
import type { ImportResult } from "@/types/result";
import { parseCsv } from "@/utils/parseCsv";

export function useFileUpload() {
  const [selectedFile, setSelectedFile] = useState<UploadedFile | null>(null);
  const [rows, setRows] = useState<CsvRow[]>([]);
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [importResult, setImportResult] = useState<ImportResult | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Derived states (prevents desynchronization bugs)
  const uploaded = Boolean(importResult);
  const downloadUrl = importResult?.downloadUrl ?? null;

  // ==========================================
  // Select & Parse CSV
  // ==========================================
  const selectFile = async (file: File) => {
    try {
      setLoading(true);
      setError(null);
      setImportResult(null);

      const isCsv =
        file.type === "text/csv" ||
        file.name.toLowerCase().endsWith(".csv") ||
        file.type === "application/vnd.ms-excel";

      if (!isCsv) {
        throw new Error("Please upload a valid CSV file (.csv).");
      }

      const parsedRows = await parseCsv(file);

      if (parsedRows.length === 0) {
        throw new Error("The selected CSV file contains no data rows.");
      }

      setRows(parsedRows);
      setSelectedFile({
        file,
        name: file.name,
        size: file.size,
        type: file.type,
      });
    } catch (err) {
      setRows([]);
      setSelectedFile(null);
      setImportResult(null);
      setError(
        err instanceof Error ? err.message : "Failed to parse CSV file."
      );
    } finally {
      setLoading(false);
    }
  };

  // ==========================================
  // Clear Selected File
  // ==========================================
  const clearFile = () => {
    setSelectedFile(null);
    setRows([]);
    setImportResult(null);
    setError(null);
  };

  // ==========================================
  // Confirm Import
  // ==========================================
  const confirmImport = async () => {
    if (!selectedFile || uploading || uploaded) {
      return;
    }

    try {
      setUploading(true);
      setError(null);

      const data: ImportResult = await uploadCsv(selectedFile.file);

      setImportResult({
        success: data.success ?? true,
        totalImported: data.totalImported ?? 0,
        totalSkipped: data.totalSkipped ?? 0,
        records: data.records ?? [],
        downloadUrl: data.downloadUrl,
      });
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : "Failed to upload CSV.";
      console.error("CSV Import Error:", errorMessage);
      setError(errorMessage);
    } finally {
      setUploading(false);
    }
  };

  return {
    selectedFile,
    rows,
    loading,
    uploading,
    uploaded,
    importResult,
    error,
    selectFile,
    clearFile,
    confirmImport,
    downloadUrl,
  };
}