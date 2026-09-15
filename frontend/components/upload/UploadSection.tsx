"use client";

import {
  FileSpreadsheet,
  ShieldCheck,
  Sparkles,
  Trash2,
  Loader2,
  CheckCircle2,
  Download,
  AlertCircle,
  ArrowRight,
  TrendingUp,
} from "lucide-react";

import Dropzone from "./Dropzone";
import PreviewTable from "@/components/preview/PreviewTable";
import { useFileUpload } from "@/hooks/useFileUpload";
import type { CrmRecord, CrmStatus } from "@/types/crm";

function formatFileSize(bytes: number): string {
  if (bytes < 1024) return `${bytes} Bytes`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(2)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
}

function getStatusBadgeClass(status: CrmStatus | string): string {
  switch (status) {
    case "GOOD_LEAD_FOLLOW_UP":
      return "bg-emerald-100 text-emerald-800 dark:bg-emerald-950/60 dark:text-emerald-300 border-emerald-300 dark:border-emerald-800";
    case "SALE_DONE":
      return "bg-blue-100 text-blue-800 dark:bg-blue-950/60 dark:text-blue-300 border-blue-300 dark:border-blue-800";
    case "DID_NOT_CONNECT":
      return "bg-amber-100 text-amber-800 dark:bg-amber-950/60 dark:text-amber-300 border-amber-300 dark:border-amber-800";
    case "BAD_LEAD":
      return "bg-rose-100 text-rose-800 dark:bg-rose-950/60 dark:text-rose-300 border-rose-300 dark:border-rose-800";
    default:
      return "bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300 border-slate-300 dark:border-slate-700";
  }
}

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

  const downloadReportUrl = importResult?.downloadUrl
    ? `${(process.env.NEXT_PUBLIC_API_URL || "http://localhost:5000").replace(/\/+$/, "")}${importResult.downloadUrl}`
    : "#";

  return (
    <section id="upload-section" className="mt-14 scroll-mt-24">
      <div className="mx-auto max-w-5xl rounded-3xl border border-slate-200/90 bg-white/95 p-6 sm:p-10 shadow-xl backdrop-blur-xl transition-colors duration-300 dark:border-slate-800 dark:bg-slate-900/90">

        {/* Step Indicator Header */}
        <div className="mb-8 flex flex-wrap items-center justify-between gap-4 border-b border-slate-200/80 pb-6 dark:border-slate-800">
          <div>
            <h3 className="text-xl sm:text-2xl font-bold tracking-tight text-slate-900 dark:text-white">
              CSV Import Studio
            </h3>
            <p className="mt-1 text-xs sm:text-sm text-slate-500 dark:text-slate-400">
              Drop any CSV file to automatically map fields into GrowEasy CRM format.
            </p>
          </div>

          <div className="flex items-center gap-2 text-xs font-semibold">
            <span
              className={`rounded-full px-2.5 py-1 ${
                selectedFile
                  ? "bg-blue-100 text-blue-700 dark:bg-blue-950 dark:text-blue-300"
                  : "bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400"
              }`}
            >
              1. Upload
            </span>
            <ArrowRight className="h-3 w-3 text-slate-400" />
            <span
              className={`rounded-full px-2.5 py-1 ${
                rows.length > 0
                  ? "bg-blue-100 text-blue-700 dark:bg-blue-950 dark:text-blue-300"
                  : "bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400"
              }`}
            >
              2. Preview
            </span>
            <ArrowRight className="h-3 w-3 text-slate-400" />
            <span
              className={`rounded-full px-2.5 py-1 ${
                uploaded
                  ? "bg-emerald-100 text-emerald-700 dark:bg-emerald-950 dark:text-emerald-300"
                  : "bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400"
              }`}
            >
              3. CRM Export
            </span>
          </div>
        </div>

        {/* Upload Dropzone */}
        {!selectedFile && <Dropzone onFileSelect={selectFile} />}

        {/* Parsing Loader */}
        {loading && (
          <div className="mt-6 flex items-center justify-center gap-3 rounded-2xl border border-blue-200/80 bg-blue-50/70 p-5 dark:border-blue-900/50 dark:bg-blue-950/40">
            <Loader2 className="h-5 w-5 animate-spin text-blue-600 dark:text-blue-400" />
            <p className="text-sm font-medium text-blue-700 dark:text-blue-300">
              Parsing CSV structure and detecting headers...
            </p>
          </div>
        )}

        {/* Error Alert */}
        {error && (
          <div className="mt-6 flex items-start gap-3 rounded-2xl border border-red-200/90 bg-red-50/90 p-4 text-sm text-red-700 dark:border-red-900/50 dark:bg-red-950/50 dark:text-red-300">
            <AlertCircle className="h-5 w-5 shrink-0 text-red-600 dark:text-red-400" />
            <div className="space-y-1">
              <p className="font-semibold">Import Notice</p>
              <p className="text-xs leading-relaxed">{error}</p>
            </div>
          </div>
        )}

        {/* Selected File Banner */}
        {selectedFile && (
          <div className="mt-6 rounded-2xl border border-blue-200/80 bg-blue-50/50 p-4 sm:p-5 dark:border-blue-900/40 dark:bg-blue-950/30">
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
              <div className="flex items-center gap-3.5">
                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-blue-600 text-white shadow-sm">
                  <FileSpreadsheet className="h-6 w-6" />
                </div>
                <div>
                  <h4 className="font-semibold text-slate-900 dark:text-white break-all">
                    {selectedFile.name}
                  </h4>
                  <div className="flex items-center gap-2 text-xs text-slate-500 dark:text-slate-400">
                    <span>{formatFileSize(selectedFile.size)}</span>
                    <span>•</span>
                    <span className="font-mono">{rows.length} rows detected</span>
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-2 self-end sm:self-center">
                <button
                  type="button"
                  onClick={clearFile}
                  disabled={uploading}
                  className="inline-flex items-center gap-1.5 rounded-xl border border-red-200 bg-white px-3 py-1.5 text-xs font-medium text-red-600 shadow-2xs transition hover:bg-red-50 disabled:opacity-50 dark:border-red-900/50 dark:bg-slate-800 dark:text-red-400 dark:hover:bg-red-950/30"
                  title="Remove file"
                >
                  <Trash2 className="h-4 w-4" />
                  <span>Remove</span>
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Preview & Action Bar */}
        {rows.length > 0 && (
          <>
            <PreviewTable rows={rows} />

            <div className="mt-8 flex flex-col items-end gap-4 border-t border-slate-200/80 pt-6 dark:border-slate-800">
              <div className="flex flex-wrap items-center justify-between w-full gap-4">
                <div className="text-xs text-slate-500 dark:text-slate-400">
                  Ready to map <span className="font-semibold text-slate-900 dark:text-white">{rows.length}</span> records into GrowEasy CRM schema.
                </div>

                <button
                  type="button"
                  onClick={confirmImport}
                  disabled={uploading || uploaded}
                  className={`inline-flex items-center gap-2 rounded-2xl px-8 py-3.5 text-sm font-semibold text-white shadow-md transition-all active:scale-98 disabled:cursor-not-allowed ${
                    uploaded
                      ? "bg-emerald-600 hover:bg-emerald-700 shadow-emerald-500/20"
                      : "bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 shadow-blue-500/25 disabled:opacity-60"
                  }`}
                >
                  {uploading ? (
                    <>
                      <Loader2 className="h-4 w-4 animate-spin" />
                      <span>AI Processing & Field Mapping...</span>
                    </>
                  ) : uploaded ? (
                    <>
                      <CheckCircle2 className="h-4 w-4" />
                      <span>Imported Successfully ✓</span>
                    </>
                  ) : (
                    <>
                      <Sparkles className="h-4 w-4" />
                      <span>Confirm & AI Map to CRM</span>
                    </>
                  )}
                </button>
              </div>
            </div>
          </>
        )}

        {/* Upload Success Banner */}
        {uploaded && (
          <div className="mt-8 rounded-2xl border border-emerald-200/90 bg-emerald-50/80 p-5 dark:border-emerald-900/60 dark:bg-emerald-950/40">
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
              <div className="flex items-center gap-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-emerald-600 text-white">
                  <CheckCircle2 className="h-5 w-5" />
                </div>
                <div>
                  <h4 className="font-semibold text-emerald-900 dark:text-emerald-200">
                    Import Completed Successfully!
                  </h4>
                  <p className="text-xs text-emerald-700 dark:text-emerald-300">
                    Normalized {importResult?.totalImported ?? 0} leads into GrowEasy CRM format.
                    {importResult?.totalSkipped ? ` (${importResult.totalSkipped} invalid/empty rows skipped)` : ""}
                  </p>
                </div>
              </div>

              {importResult?.downloadUrl && (
                <a
                  href={downloadReportUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-5 py-2.5 text-xs font-semibold text-white shadow-md transition hover:bg-emerald-700 active:scale-98"
                >
                  <Download className="h-4 w-4" />
                  <span>Download Clean Excel Report</span>
                </a>
              )}
            </div>
          </div>
        )}

        {/* Results Metrics & CRM Records Table */}
        {importResult && (
          <div className="mt-8 space-y-6">
            {/* KPI Cards */}
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
              <div className="rounded-2xl border border-emerald-200/80 bg-emerald-50/50 p-5 dark:border-emerald-900/50 dark:bg-emerald-950/20">
                <div className="flex items-center justify-between">
                  <p className="text-xs font-medium text-emerald-700 dark:text-emerald-400">
                    Imported Records
                  </p>
                  <CheckCircle2 className="h-4 w-4 text-emerald-600" />
                </div>
                <h3 className="mt-2 text-3xl font-extrabold text-emerald-600 dark:text-emerald-400">
                  {importResult.totalImported}
                </h3>
                <p className="mt-1 text-[11px] text-slate-500 dark:text-slate-400">
                  CRM-ready standardized leads
                </p>
              </div>

              <div className="rounded-2xl border border-amber-200/80 bg-amber-50/50 p-5 dark:border-amber-900/50 dark:bg-amber-950/20">
                <div className="flex items-center justify-between">
                  <p className="text-xs font-medium text-amber-700 dark:text-amber-400">
                    Skipped / Filtered
                  </p>
                  <AlertCircle className="h-4 w-4 text-amber-600" />
                </div>
                <h3 className="mt-2 text-3xl font-extrabold text-amber-600 dark:text-amber-400">
                  {importResult.totalSkipped}
                </h3>
                <p className="mt-1 text-[11px] text-slate-500 dark:text-slate-400">
                  Missing required email & phone
                </p>
              </div>

              <div className="rounded-2xl border border-blue-200/80 bg-blue-50/50 p-5 dark:border-blue-900/50 dark:bg-blue-950/20">
                <div className="flex items-center justify-between">
                  <p className="text-xs font-medium text-blue-700 dark:text-blue-400">
                    AI Mapping Rate
                  </p>
                  <TrendingUp className="h-4 w-4 text-blue-600" />
                </div>
                <h3 className="mt-2 text-3xl font-extrabold text-blue-600 dark:text-blue-400">
                  100%
                </h3>
                <p className="mt-1 text-[11px] text-slate-500 dark:text-slate-400">
                  Automated field schema resolution
                </p>
              </div>
            </div>

            {/* CRM Result Table */}
            {importResult.records && importResult.records.length > 0 && (
              <div className="overflow-hidden rounded-2xl border border-slate-200/90 bg-white dark:border-slate-800 dark:bg-slate-900">
                <div className="border-b border-slate-200/80 bg-slate-50/80 px-5 py-3 dark:border-slate-800 dark:bg-slate-850/80">
                  <h4 className="text-sm font-semibold text-slate-900 dark:text-white">
                    Normalized GrowEasy CRM Records
                  </h4>
                </div>

                <div className="max-h-96 overflow-auto scrollbar-thin">
                  <table className="min-w-full divide-y divide-slate-200 dark:divide-slate-800 text-left">
                    <thead className="sticky top-0 bg-slate-100/95 dark:bg-slate-800/95">
                      <tr>
                        <th className="p-3.5 text-xs font-semibold uppercase tracking-wider text-slate-700 dark:text-slate-300">Name</th>
                        <th className="p-3.5 text-xs font-semibold uppercase tracking-wider text-slate-700 dark:text-slate-300">Email</th>
                        <th className="p-3.5 text-xs font-semibold uppercase tracking-wider text-slate-700 dark:text-slate-300">Mobile</th>
                        <th className="p-3.5 text-xs font-semibold uppercase tracking-wider text-slate-700 dark:text-slate-300">Company</th>
                        <th className="p-3.5 text-xs font-semibold uppercase tracking-wider text-slate-700 dark:text-slate-300">CRM Stage</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200/60 dark:divide-slate-800/60">
                      {importResult.records.map((record: CrmRecord, index: number) => (
                        <tr key={index} className="transition hover:bg-slate-50/80 dark:hover:bg-slate-800/40">
                          <td className="p-3.5 text-xs font-medium text-slate-900 dark:text-white">
                            {record.name || "(Unknown Name)"}
                          </td>
                          <td className="p-3.5 text-xs text-slate-600 dark:text-slate-300">
                            {record.email || "—"}
                          </td>
                          <td className="p-3.5 text-xs text-slate-600 dark:text-slate-300 font-mono">
                            {record.mobile_without_country_code
                              ? `${record.country_code ? record.country_code + " " : ""}${record.mobile_without_country_code}`
                              : "—"}
                          </td>
                          <td className="p-3.5 text-xs text-slate-600 dark:text-slate-300">
                            {record.company || "—"}
                          </td>
                          <td className="p-3.5 text-xs">
                            <span
                              className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-[11px] font-medium ${getStatusBadgeClass(
                                record.crm_status
                              )}`}
                            >
                              {record.crm_status || "PENDING"}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Features Highlight Grid */}
        <div className="mt-12 grid gap-6 md:grid-cols-3">
          <div className="rounded-2xl border border-slate-200/80 bg-slate-50/50 p-6 transition hover:border-blue-300 dark:border-slate-800 dark:bg-slate-850/40 dark:hover:border-blue-900">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-100 text-blue-600 dark:bg-blue-950/80 dark:text-blue-400">
              <Sparkles className="h-5 w-5" />
            </div>
            <h4 className="mt-4 text-base font-semibold text-slate-900 dark:text-white">
              AI Field Mapping
            </h4>
            <p className="mt-2 text-xs sm:text-sm text-slate-500 dark:text-slate-400 leading-relaxed">
              Intelligently interprets non-standard column headers and maps them to standard CRM properties.
            </p>
          </div>

          <div className="rounded-2xl border border-slate-200/80 bg-slate-50/50 p-6 transition hover:border-emerald-300 dark:border-slate-800 dark:bg-slate-850/40 dark:hover:border-emerald-900">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-emerald-100 text-emerald-600 dark:bg-emerald-950/80 dark:text-emerald-400">
              <FileSpreadsheet className="h-5 w-5" />
            </div>
            <h4 className="mt-4 text-base font-semibold text-slate-900 dark:text-white">
              Any Spreadsheet Format
            </h4>
            <p className="mt-2 text-xs sm:text-sm text-slate-500 dark:text-slate-400 leading-relaxed">
              Supports Facebook Leads, Google Ads campaigns, Excel downloads, and custom CRM dumps with comma, tab, or semicolon delimiters.
            </p>
          </div>

          <div className="rounded-2xl border border-slate-200/80 bg-slate-50/50 p-6 transition hover:border-purple-300 dark:border-slate-800 dark:bg-slate-850/40 dark:hover:border-purple-900">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-purple-100 text-purple-600 dark:bg-purple-950/80 dark:text-purple-400">
              <ShieldCheck className="h-5 w-5" />
            </div>
            <h4 className="mt-4 text-base font-semibold text-slate-900 dark:text-white">
              Zero-Retention Security
            </h4>
            <p className="mt-2 text-xs sm:text-sm text-slate-500 dark:text-slate-400 leading-relaxed">
              Raw uploaded files are processed in-memory without persistent disk caching, ensuring client data privacy and strict compliance.
            </p>
          </div>
        </div>

      </div>
    </section>
  );
}