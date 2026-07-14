"use client";

import { useState } from "react";

import { uploadCsv } from "@/services/upload.service";

import type { UploadedFile } from "@/types/upload";
import type { CsvRow } from "@/types/csv";
import type { ImportResult } from "@/types/result";

import { parseCsv } from "@/utils/parseCsv";

export function useFileUpload() {
  const [selectedFile, setSelectedFile] =
    useState<UploadedFile | null>(null);

  const [rows, setRows] = useState<CsvRow[]>([]);

  const [loading, setLoading] = useState(false);

  const [uploading, setUploading] = useState(false);

  const [uploaded, setUploaded] = useState(false);

  const [importResult, setImportResult] = useState<{
  totalImported: number;
  totalSkipped: number;
  records: any[];
  downloadUrl?: string;
} | null>(null);
  const [downloadUrl, setDownloadUrl] = useState<string | null>(null);

  const [error, setError] = useState<string | null>(null);

  // ==========================================
  // Select & Parse CSV
  // ==========================================

  const selectFile = async (file: File) => {
    try {
      setLoading(true);
      setError(null);
      setUploaded(false);
      setImportResult(null);
      setDownloadUrl(null);

      // Validate CSV

      if (
        file.type !== "text/csv" &&
        !file.name.toLowerCase().endsWith(".csv")
      ) {
        throw new Error("Please upload a valid CSV file.");
      }

      // Parse CSV

      const parsedRows = await parseCsv(file);

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
        err instanceof Error
          ? err.message
          : "Failed to parse CSV file."
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
    setUploaded(false);
    setImportResult(null);
    setDownloadUrl(null);
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

      const data: ImportResult = await uploadCsv(
        selectedFile.file
         );
         setDownloadUrl(data.downloadUrl ?? null);

      setImportResult({
        success: data.success ?? true,
        totalImported: data.totalImported ?? 0,
        totalSkipped: data.totalSkipped ?? 0,
        records: data.records ?? [],
        downloadUrl: data.downloadUrl,
      });

      setUploaded(true);
    } catch (err) {
      const errorMessage =
        err instanceof Error
          ? err.message
          : "Failed to upload CSV.";

      console.error("CSV Upload Error:", errorMessage);

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