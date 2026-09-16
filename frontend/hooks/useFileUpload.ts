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

      const fileName = file.name.toLowerCase();
      const isCsv =
        file.type === "text/csv" ||
        fileName.endsWith(".csv") ||
        file.type === "application/vnd.ms-excel" ||
        file.type === "text/plain";

      const isDocx =
        fileName.endsWith(".docx") ||
        file.type.includes("wordprocessingml") ||
        file.type.includes("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

      if (!isCsv && !isDocx) {
        throw new Error("Supported file types are CSV and DOCX.");
      }

      if (isCsv) {
        const parsedRows = await parseCsv(file);
        if (parsedRows.length === 0) {
          throw new Error("The selected CSV file contains no data rows.");
        }
        setRows(parsedRows);
      } else {
        // Word DOCX document: unstructured text/table extraction handled securely on backend
        setRows([]);
      }

      setSelectedFile({
        file,
        name: file.name,
        size: file.size,
        type: file.type || (isDocx ? "application/vnd.openxmlformats-officedocument.wordprocessingml.document" : "text/csv"),
      });
    } catch (err) {
      setRows([]);
      setSelectedFile(null);
      setImportResult(null);
      setError(
        err instanceof Error ? err.message : "Failed to parse file."
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

      const totalImp = data.processedRecords ?? data.totalImported ?? 0;
      const totalSkip = data.skippedRecords ?? data.totalSkipped ?? 0;
      const totalRecs = data.totalRecords ?? (totalImp + totalSkip);
      const procPct = data.processingPercentage ?? (totalRecs > 0 ? Math.round((totalImp * 1000.0) / totalRecs) / 10 : 100.0);

      setImportResult({
        success: data.success ?? true,
        totalImported: totalImp,
        totalSkipped: totalSkip,
        totalRecords: totalRecs,
        processedRecords: totalImp,
        skippedRecords: totalSkip,
        processingPercentage: procPct,
        records: data.records ?? [],
        downloadUrl: data.downloadUrl,
        duplicateCount: data.duplicateCount ?? 0,
        invalidEmailCount: data.invalidEmailCount ?? 0,
        missingFieldCount: data.missingFieldCount ?? 0,
        averageQualityScore: data.averageQualityScore ?? 0,
        leadStatusDistribution: data.leadStatusDistribution ?? {},
        dataQualitySummary: data.dataQualitySummary,
        errors: data.errors ?? [],
      });
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : "Failed to upload file.";
      console.error("Import Error:", errorMessage);
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