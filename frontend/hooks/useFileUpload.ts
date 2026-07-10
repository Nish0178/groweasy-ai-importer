"use client";

import { useState } from "react";
import type { UploadedFile } from "@/types/upload";
import type { CsvRow } from "@/types/csv";
import { parseCsv } from "@/utils/parseCsv";

export function useFileUpload() {
  const [selectedFile, setSelectedFile] = useState<UploadedFile | null>(null);
  const [rows, setRows] = useState<CsvRow[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const selectFile = async (file: File) => {
    try {
      setLoading(true);
      setError(null);

      // Only allow CSV files
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

      setError(
        err instanceof Error
          ? err.message
          : "Failed to parse CSV file."
      );
    } finally {
      setLoading(false);
    }
  };

  const clearFile = () => {
    setSelectedFile(null);
    setRows([]);
    setError(null);
  };

  return {
    selectedFile,
    rows,
    loading,
    error,
    selectFile,
    clearFile,
  };
}