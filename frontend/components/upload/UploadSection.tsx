"use client";

import PreviewTable from "@/components/preview/PreviewTable";
import { FileSpreadsheet, ShieldCheck, Sparkles, Trash2 } from "lucide-react";
import Dropzone from "./Dropzone";
import { useFileUpload } from "@/hooks/useFileUpload";

export default function UploadSection() {
  const { selectedFile, selectFile, clearFile } = useFileUpload();

  const formatFileSize = (bytes: number) => {
    if (bytes < 1024) return `${bytes} Bytes`;
    if (bytes < 1024 * 1024)
      return `${(bytes / 1024).toFixed(2)} KB`;

    return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
  };

  return (
    <section className="mt-16">
      <div className="mx-auto max-w-5xl rounded-3xl border border-slate-200 bg-white p-10 shadow-xl">
        {/* Upload Area */}
        <Dropzone onFileSelect={selectFile} />

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
                className="rounded-lg p-2 text-red-500 transition hover:bg-red-100"
              >
                <Trash2 className="h-5 w-5" />
              </button>
            </div>
          </div>
        )}

        {/* Features */}
        <div className="mt-10 grid gap-6 md:grid-cols-3">
          <div className="rounded-2xl border p-6">
            <Sparkles className="mb-4 h-8 w-8 text-blue-600" />

            <h3 className="text-lg font-semibold">
              AI Field Mapping
            </h3>

            <p className="mt-2 text-sm text-slate-600">
              Automatically understands different column names and maps them into CRM fields.
            </p>
          </div>

          <div className="rounded-2xl border p-6">
            <FileSpreadsheet className="mb-4 h-8 w-8 text-green-600" />

            <h3 className="text-lg font-semibold">
              Works With Any CSV
            </h3>

            <p className="mt-2 text-sm text-slate-600">
              Facebook Leads, Google Ads, Excel exports, CRM exports, and custom spreadsheets.
            </p>
          </div>

          <div className="rounded-2xl border p-6">
            <ShieldCheck className="mb-4 h-8 w-8 text-purple-600" />

            <h3 className="text-lg font-semibold">
              Secure Processing
            </h3>

            <p className="mt-2 text-sm text-slate-600">
              Your uploaded files stay secure throughout the import process.
            </p>
          </div>
        </div>
      </div>
    </section>
  );
}