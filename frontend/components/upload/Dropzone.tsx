"use client";

import { useCallback } from "react";
import { useDropzone } from "react-dropzone";
import { UploadCloud, FileSpreadsheet, Sparkles } from "lucide-react";

interface DropzoneProps {
  readonly onFileSelect: (file: File) => void;
}

export default function Dropzone({ onFileSelect }: DropzoneProps) {
  const onDrop = useCallback(
    (acceptedFiles: File[]) => {
      if (acceptedFiles.length > 0) {
        onFileSelect(acceptedFiles[0]);
      }
    },
    [onFileSelect]
  );

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    accept: {
      "text/csv": [".csv"],
      "application/vnd.ms-excel": [".csv"],
      "text/plain": [".csv"],
    },
    multiple: false,
    onDrop,
  });

  const loadSampleDataset = (type: "real_estate" | "b2b") => {
    let csvData = "";
    let fileName = "";

    if (type === "real_estate") {
      fileName = "facebook_real_estate_leads.csv";
      csvData =
        "Full Name,Email,Phone,Project Interest,City,State,Budget,Status\n" +
        "Rajesh Varma,rajesh.varma@gmail.com,9845012345,Eden Park 3BHK,Bangalore,Karnataka,1.2 Cr,Interested\n" +
        "Sunita Rao,sunita.rao@outlook.com,+91 9980112233,Meridian Tower,Hyderabad,Telangana,85 Lakhs,Requested Site Visit\n" +
        "Amitabh Sen,amitabh.sen@yahoo.co.in,9811223344,Sarjapur Plots,Bangalore,Karnataka,65 Lakhs,Follow Up Next Week\n" +
        "Kavita Nair,kavita.nair@corp.in,9740556677,Eden Park Penthouse,Chennai,Tamil Nadu,2.5 Cr,Meeting Scheduled\n";
    } else {
      fileName = "google_ads_b2b_inquiries.csv";
      csvData =
        "Contact Person,Work Email,Mobile Number,Company,Location,Requirement,Stage\n" +
        "David Miller,david@nexusscale.com,4155550144,NexusScale Inc,San Francisco,Enterprise CRM Integration,Discovery Call\n" +
        "Aarav Mehta,aarav@fintechgrowth.io,+91 9820099112,Fintech Growth Labs,Mumbai,Data pipeline automation,Qualified Lead\n" +
        "Elena Rostova,elena@globaltech.eu,+44 2079460912,GlobalTech Europe,London,CSV batch imports,Demo Completed\n" +
        "Vikram Singhania,vikram@singhaniagroup.com,9810055443,Singhania Group,New Delhi,Full custom deployment,Contract Sent\n";
    }

    const file = new File([csvData], fileName, { type: "text/csv" });
    onFileSelect(file);
  };

  return (
    <div>
      <div
        {...getRootProps()}
        className={`group relative cursor-pointer rounded-2xl border-2 border-dashed p-10 sm:p-14 text-center transition-all duration-300 ${
          isDragActive
            ? "border-blue-500 bg-blue-50/80 dark:border-blue-400 dark:bg-blue-950/40 scale-[1.01] shadow-lg shadow-blue-500/10"
            : "border-slate-300/90 bg-slate-50/60 hover:border-blue-400 hover:bg-blue-50/30 dark:border-slate-700/80 dark:bg-slate-850/50 dark:hover:border-blue-500/60 dark:hover:bg-slate-800/60"
        }`}
      >
        <input {...getInputProps()} />

        {/* Upload Icon with animated glow */}
        <div className="relative mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-blue-100 text-blue-600 shadow-sm transition-transform duration-300 group-hover:scale-110 dark:bg-blue-950/80 dark:text-blue-400">
          <UploadCloud className="h-8 w-8" />
        </div>

        <h2 className="mt-5 text-xl sm:text-2xl font-bold tracking-tight text-slate-900 dark:text-white">
          {isDragActive ? "Release to drop your CSV" : "Drag & drop your CSV file here"}
        </h2>

        <p className="mt-2 text-sm text-slate-500 dark:text-slate-400">
          or <span className="font-semibold text-blue-600 underline underline-offset-2 dark:text-blue-400">click to browse</span> from your computer
        </p>

        <div className="mt-6 inline-flex items-center gap-2 rounded-lg bg-white/80 px-3 py-1 text-xs font-medium text-slate-500 shadow-2xs border border-slate-200/80 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-400">
          <FileSpreadsheet className="h-3.5 w-3.5 text-blue-500" />
          Supports .CSV up to 10MB (UTF-8, comma/semicolon/tab separated)
        </div>
      </div>

      {/* One-Click Sample Presets */}
      <div className="mt-4 flex flex-wrap items-center justify-between gap-3 px-2">
        <div className="flex items-center gap-1.5 text-xs text-slate-500 dark:text-slate-400">
          <Sparkles className="h-3.5 w-3.5 text-amber-500" />
          <span>Don&apos;t have a file ready? Test with sample data:</span>
        </div>
        <div className="flex flex-wrap gap-2">
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              loadSampleDataset("real_estate");
            }}
            className="inline-flex items-center gap-1.5 rounded-lg border border-slate-200/80 bg-white px-2.5 py-1 text-xs font-medium text-slate-700 shadow-2xs transition hover:border-blue-400 hover:text-blue-600 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200 dark:hover:border-blue-400 dark:hover:text-blue-300"
          >
            <span>🏠 Real Estate Leads CSV</span>
          </button>
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              loadSampleDataset("b2b");
            }}
            className="inline-flex items-center gap-1.5 rounded-lg border border-slate-200/80 bg-white px-2.5 py-1 text-xs font-medium text-slate-700 shadow-2xs transition hover:border-blue-400 hover:text-blue-600 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200 dark:hover:border-blue-400 dark:hover:text-blue-300"
          >
            <span>💼 B2B Inquiries CSV</span>
          </button>
        </div>
      </div>
    </div>
  );
}