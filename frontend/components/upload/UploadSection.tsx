"use client";

import { UploadCloud, FileSpreadsheet, ShieldCheck, Sparkles } from "lucide-react";

export default function UploadSection() {
  return (
    <section className="mt-16">

      <div className="mx-auto max-w-5xl rounded-3xl border border-slate-200 bg-white shadow-xl p-10">

        {/* Upload Box */}
        <div className="rounded-2xl border-2 border-dashed border-blue-300 bg-blue-50/40 p-16 text-center transition-all duration-300 hover:border-blue-500 hover:bg-blue-50">

          <div className="mx-auto flex h-24 w-24 items-center justify-center rounded-full bg-white shadow-md">
            <UploadCloud className="h-12 w-12 text-blue-600" />
          </div>

          <h2 className="mt-8 text-3xl font-bold text-slate-800">
            Drag & Drop your CSV here
          </h2>

          <p className="mt-3 text-slate-600">
            Upload any CSV format and let AI automatically map it
            into GrowEasy CRM fields.
          </p>

          <button
            className="mt-8 rounded-xl bg-blue-600 px-8 py-4 text-white font-semibold transition hover:bg-blue-700"
          >
            Browse CSV
          </button>

          <p className="mt-5 text-sm text-slate-500">
            Supported format: <strong>.csv</strong>
          </p>

        </div>

        {/* Features */}
        <div className="mt-10 grid gap-6 md:grid-cols-3">

          <div className="rounded-2xl border p-6">
            <Sparkles className="mb-4 h-8 w-8 text-blue-600" />

            <h3 className="font-semibold text-lg">
              AI Field Mapping
            </h3>

            <p className="mt-2 text-sm text-slate-600">
              Automatically detects columns even if names are completely different.
            </p>
          </div>

          <div className="rounded-2xl border p-6">

            <FileSpreadsheet className="mb-4 h-8 w-8 text-green-600" />

            <h3 className="font-semibold text-lg">
              Works with Any CSV
            </h3>

            <p className="mt-2 text-sm text-slate-600">
              Facebook Leads, Google Ads, Excel exports,
              CRM exports and more.
            </p>

          </div>

          <div className="rounded-2xl border p-6">

            <ShieldCheck className="mb-4 h-8 w-8 text-purple-600" />

            <h3 className="font-semibold text-lg">
              Secure Processing
            </h3>

            <p className="mt-2 text-sm text-slate-600">
              Your uploaded files are processed securely
              and never exposed publicly.
            </p>

          </div>

        </div>

      </div>

    </section>
  );
}