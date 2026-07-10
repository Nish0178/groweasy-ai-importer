"use client";

import { UploadCloud } from "lucide-react";

export default function UploadSection() {
  return (
    <div className="mt-12">
      <div className="border-2 border-dashed rounded-xl p-16 text-center hover:border-primary transition">
        <UploadCloud className="mx-auto h-14 w-14 text-primary" />

        <h3 className="mt-6 text-xl font-semibold">
          Drag & Drop your CSV here
        </h3>

        <p className="mt-2 text-muted-foreground">
          or click to browse your files
        </p>

        <button className="mt-8 rounded-lg bg-black text-white px-6 py-3">
          Browse CSV
        </button>
      </div>

      <div className="mt-8 flex flex-wrap justify-center gap-3">
        <span className="rounded-full border px-4 py-2">
          Facebook Leads
        </span>

        <span className="rounded-full border px-4 py-2">
          Google Ads
        </span>

        <span className="rounded-full border px-4 py-2">
          Excel Sheets
        </span>

        <span className="rounded-full border px-4 py-2">
          CRM Export
        </span>
      </div>
    </div>
  );
}