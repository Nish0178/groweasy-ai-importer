"use client";

import {
  FileSpreadsheet,
  ShieldCheck,
  Sparkles,
  Trash2,
  Loader2,
} from "lucide-react";

import Dropzone from "./Dropzone";
import PreviewTable from "@/components/preview/PreviewTable";
import { useFileUpload } from "@/hooks/useFileUpload";

export default function UploadSection() {
  const {
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
  } = useFileUpload();

  const formatFileSize = (bytes: number) => {
    if (bytes < 1024) return `${bytes} Bytes`;

    if (bytes < 1024 * 1024)
      return `${(bytes / 1024).toFixed(2)} KB`;

    return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
  };

  return (
    <section className="mt-16">
      <div className="mx-auto max-w-5xl rounded-3xl border border-slate-200 bg-white p-10 shadow-xl">

        {/* Upload */}
        <Dropzone onFileSelect={selectFile} />

        {/* Loading */}

        {loading && (
          <div className="mt-6 flex items-center justify-center gap-3 rounded-xl bg-blue-50 p-4">

            <Loader2 className="h-5 w-5 animate-spin text-blue-600" />

            <p className="font-medium text-blue-700">
              Parsing CSV...
            </p>

          </div>
        )}

        {/* Error */}

        {error && (
          <div className="mt-6 rounded-xl border border-red-200 bg-red-50 p-4 text-red-600">
            {error}
          </div>
        )}

        {/* Selected File */}

        {selectedFile && (
          <div className="mt-8 rounded-2xl border border-green-200 bg-green-50 p-5">

            <div className="flex items-center justify-between">

              <div className="flex items-center gap-4">

                <FileSpreadsheet className="h-10 w-10 text-green-600" />

                <div>

                  <h3 className="font-semibold">
                    {selectedFile.name}
                  </h3>

                  <p className="text-sm text-slate-600">
                    {formatFileSize(selectedFile.size)}
                  </p>

                </div>

              </div>

              <button
                onClick={clearFile}
                className="rounded-lg p-2 transition hover:bg-red-100"
              >
                <Trash2 className="h-5 w-5 text-red-500" />
              </button>

            </div>

          </div>
        )}

        {/* Preview */}

        {rows.length > 0 && (
          <>
            <PreviewTable rows={rows} />

            <div className="mt-8 flex flex-col items-end gap-4">
              {error && (
                <div className="w-full rounded-xl border border-red-200 bg-red-50 p-4 text-red-600">
                  ⚠️ {error}
                </div>
              )}
              <div className="flex items-center gap-4">
                <button
                  onClick={confirmImport}
                  disabled={uploading || uploaded}
                  className={`rounded-xl px-8 py-3 font-semibold text-white transition disabled:cursor-not-allowed ${
                    uploaded
                      ? "bg-green-600 disabled:opacity-100"
                      : "bg-blue-600 hover:bg-blue-700 disabled:opacity-50"
                  }`}
                >
                  {uploading ? (
                    <div className="flex items-center gap-2">
                      <Loader2 className="h-4 w-4 animate-spin" />
                      Processing AI Import...
                    </div>
                  ) : uploaded ? (
                    "Imported Successfully ✓"
                  ) : (
                    "Confirm Import"
                  )}
                </button>
              </div>
              {uploaded && (
                <div className="w-full rounded-xl border border-green-200 bg-green-50 p-4 text-green-700">
                  ✅ Successfully imported {importResult?.totalImported ?? 0} CRM records!
                  {importResult?.totalSkipped ? ` (${importResult.totalSkipped} skipped)` : ""}
                </div>
              )}
            </div>
          </>
        )}

        {/* Features */}

        <div className="mt-10 grid gap-6 md:grid-cols-3">

          <div className="rounded-2xl border p-6">

            <Sparkles className="mb-4 h-8 w-8 text-blue-600" />

            <h3 className="text-lg font-semibold">
              AI Field Mapping
            </h3>

            <p className="mt-2 text-sm text-slate-600">
              Automatically detects different column names and maps them into CRM fields.
            </p>

          </div>

          <div className="rounded-2xl border p-6">

            <FileSpreadsheet className="mb-4 h-8 w-8 text-green-600" />

            <h3 className="text-lg font-semibold">
              Works with Any CSV
            </h3>

            <p className="mt-2 text-sm text-slate-600">
              Facebook Leads, Google Ads, Excel exports, CRM exports and custom spreadsheets.
            </p>

          </div>

          <div className="rounded-2xl border p-6">

            <ShieldCheck className="mb-4 h-8 w-8 text-purple-600" />

            <h3 className="text-lg font-semibold">
              Secure Processing
            </h3>

            <p className="mt-2 text-sm text-slate-600">
              Files remain secure during processing.
            </p>

          </div>

        </div>

      </div>
    </section>
  );
}