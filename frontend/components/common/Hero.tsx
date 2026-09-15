"use client";

import { Sparkles, Database, BrainCircuit, ArrowDown, CheckCircle2 } from "lucide-react";

export default function Hero() {
  return (
    <section className="relative py-12 md:py-20 text-center">
      {/* Background Ambient Glow */}
      <div className="pointer-events-none absolute inset-x-0 -top-20 flex justify-center overflow-hidden">
        <div className="h-64 w-[600px] rounded-full bg-gradient-to-tr from-blue-500/15 via-indigo-500/15 to-purple-500/15 blur-3xl dark:from-blue-600/20 dark:via-indigo-600/20 dark:to-purple-600/20" />
      </div>

      {/* Pill Badge */}
      <div className="inline-flex items-center gap-2 rounded-full border border-blue-200/80 bg-blue-50/80 px-4 py-1.5 text-xs font-semibold text-blue-700 shadow-xs backdrop-blur-xs transition hover:scale-105 dark:border-blue-900/50 dark:bg-blue-950/60 dark:text-blue-300">
        <span className="flex h-2 w-2 rounded-full bg-blue-600 dark:bg-blue-400" />
        <Sparkles size={14} className="text-blue-600 dark:text-blue-400" />
        AI-Powered Schema Extraction & Normalization
      </div>

      {/* Main Heading */}
      <h1 className="mt-8 text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-extrabold tracking-tight text-slate-900 dark:text-white">
        Turn Messy Spreadsheets Into{" "}
        <span className="bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 bg-clip-text text-transparent dark:from-blue-400 dark:via-indigo-400 dark:to-purple-400">
          Clean CRM Leads
        </span>
      </h1>

      {/* Subheading */}
      <p className="mt-6 mx-auto max-w-2xl text-base sm:text-lg md:text-xl text-slate-600 dark:text-slate-300 leading-relaxed">
        Import Facebook Leads, Google Ads exports, Excel sheets, Real Estate CRM dumps, or custom spreadsheets.
        Our intelligent AI model accurately maps, cleans, and standardizes every lead into GrowEasy CRM format in seconds.
      </p>

      {/* Action Buttons */}
      <div className="mt-10 flex flex-wrap items-center justify-center gap-4">
        <a
          href="#upload-section"
          className="inline-flex items-center gap-2 rounded-2xl bg-gradient-to-r from-blue-600 to-indigo-600 px-7 py-3.5 text-sm font-semibold text-white shadow-lg shadow-blue-500/25 transition hover:from-blue-700 hover:to-indigo-700 hover:shadow-xl active:scale-98 dark:shadow-blue-500/15"
        >
          <Sparkles size={16} />
          Upload & Map CSV
          <ArrowDown size={15} />
        </a>

        <a
          href="#how-it-works"
          className="inline-flex items-center gap-2 rounded-2xl border border-slate-200/90 bg-white/90 px-6 py-3.5 text-sm font-semibold text-slate-700 shadow-xs transition hover:bg-slate-50 hover:text-slate-900 active:scale-98 dark:border-slate-800 dark:bg-slate-800/90 dark:text-slate-200 dark:hover:bg-slate-750 dark:hover:text-white"
        >
          See How It Works
        </a>
      </div>

      {/* Feature Badges */}
      <div className="mt-12 flex flex-wrap justify-center gap-3 text-xs sm:text-sm">
        <div className="inline-flex items-center gap-2 rounded-xl border border-slate-200/80 bg-white/70 px-4 py-2 text-slate-700 shadow-xs dark:border-slate-800 dark:bg-slate-850/80 dark:text-slate-300">
          <BrainCircuit size={16} className="text-indigo-500" />
          <span>Zero-Config AI Mapping</span>
        </div>

        <div className="inline-flex items-center gap-2 rounded-xl border border-slate-200/80 bg-white/70 px-4 py-2 text-slate-700 shadow-xs dark:border-slate-800 dark:bg-slate-850/80 dark:text-slate-300">
          <Database size={16} className="text-blue-500" />
          <span>GrowEasy CRM Ready</span>
        </div>

        <div className="inline-flex items-center gap-2 rounded-xl border border-slate-200/80 bg-white/70 px-4 py-2 text-slate-700 shadow-xs dark:border-slate-800 dark:bg-slate-850/80 dark:text-slate-300">
          <CheckCircle2 size={16} className="text-emerald-500" />
          <span>Auto Phone & Email Cleanse</span>
        </div>
      </div>
    </section>
  );
}