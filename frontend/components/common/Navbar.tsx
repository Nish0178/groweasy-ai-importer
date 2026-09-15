"use client";

import React from "react";
import { FileSpreadsheet, Cpu } from "lucide-react";
import ThemeToggle from "@/components/theme/ThemeToggle";

export default function Navbar() {
  const downloadSampleCsv = () => {
    const csvContent =
      "Full Name,Email Address,Phone Number,Company Name,City,State,Notes\n" +
      "Arjun Sharma,arjun.sharma@techcorp.com,9876543210,TechCorp India,Bangalore,Karnataka,Interested in commercial real estate plots\n" +
      "Priya Patel,priya.p@innovate.io,+91 9123456780,Innovate Labs,Mumbai,Maharashtra,Requested quote for 3BHK Eden Park\n" +
      "Michael Scott,michael@dundermifflin.com,5550199283,Dunder Mifflin,Scranton,Pennsylvania,Budget around 80L immediate booking\n" +
      "Ananya Roy,ananya.roy@designco.in,9988776655,Design Studio,Delhi,NCR,Follow up next Monday regarding site visit\n";

    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.setAttribute("href", url);
    link.setAttribute("download", "sample_leads.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b border-slate-200/80 bg-white/80 backdrop-blur-md transition-colors duration-300 dark:border-slate-800/80 dark:bg-slate-900/80">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
        {/* Brand Logo */}
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-tr from-blue-600 to-indigo-600 text-white shadow-md shadow-blue-500/20">
            <Cpu className="h-5 w-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-lg font-bold tracking-tight text-slate-900 dark:text-white">
                GrowEasy
              </span>
              <span className="inline-flex items-center rounded-md bg-blue-50 px-1.5 py-0.5 text-xs font-semibold text-blue-700 ring-1 ring-blue-700/10 dark:bg-blue-950/60 dark:text-blue-300 dark:ring-blue-400/20">
                AI Importer
              </span>
            </div>
            <p className="hidden text-[11px] text-slate-500 sm:block dark:text-slate-400">
              Smart CRM Lead Normalization
            </p>
          </div>
        </div>

        {/* Navigation Links */}
        <nav className="hidden items-center gap-6 md:flex">
          <a
            href="#upload-section"
            className="text-sm font-medium text-slate-600 transition hover:text-blue-600 dark:text-slate-300 dark:hover:text-blue-400"
          >
            Upload CSV
          </a>
          <a
            href="#how-it-works"
            className="text-sm font-medium text-slate-600 transition hover:text-blue-600 dark:text-slate-300 dark:hover:text-blue-400"
          >
            How it Works
          </a>
          <a
            href="#stats"
            className="text-sm font-medium text-slate-600 transition hover:text-blue-600 dark:text-slate-300 dark:hover:text-blue-400"
          >
            Stats & Schema
          </a>
        </nav>

        {/* Actions & Theme Toggle */}
        <div className="flex items-center gap-3">
          {/* Quick Sample CSV download */}
          <button
            type="button"
            onClick={downloadSampleCsv}
            className="hidden items-center gap-1.5 rounded-xl border border-slate-200/80 bg-slate-50 px-3 py-1.5 text-xs font-medium text-slate-700 shadow-xs transition hover:bg-slate-100 sm:inline-flex dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200 dark:hover:bg-slate-700"
            title="Download a demo sample CSV"
          >
            <FileSpreadsheet className="h-3.5 w-3.5 text-emerald-500" />
            <span>Sample CSV</span>
          </button>

          {/* Theme Toggle in Navbar */}
          <ThemeToggle showLabel={true} />

          {/* Status Indicator */}
          <div className="hidden items-center gap-1.5 rounded-full border border-emerald-200/80 bg-emerald-50/80 px-2.5 py-1 text-[11px] font-medium text-emerald-700 lg:flex dark:border-emerald-900/40 dark:bg-emerald-950/40 dark:text-emerald-400">
            <span className="relative flex h-2 w-2">
              <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-emerald-400 opacity-75"></span>
              <span className="relative inline-flex h-2 w-2 rounded-full bg-emerald-500"></span>
            </span>
            <span>AI Ready</span>
          </div>
        </div>
      </div>
    </header>
  );
}
