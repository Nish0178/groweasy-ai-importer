"use client";

import React from "react";
import { Cpu, ShieldCheck, Zap, Heart } from "lucide-react";
import ThemeToggle from "@/components/theme/ThemeToggle";

export default function Footer() {
  return (
    <footer className="mt-28 border-t border-slate-200/80 bg-white/60 py-12 backdrop-blur-xs transition-colors duration-300 dark:border-slate-800/80 dark:bg-slate-900/60">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col items-center justify-between gap-6 sm:flex-row">
          <div className="flex items-center gap-3">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-blue-600 text-white shadow-sm">
              <Cpu className="h-4 w-4" />
            </div>
            <div>
              <p className="text-sm font-semibold text-slate-900 dark:text-white">
                GrowEasy AI Importer
              </p>
              <p className="text-xs text-slate-500 dark:text-slate-400">
                Automated CRM lead transformation & validation engine
              </p>
            </div>
          </div>

          <div className="flex items-center gap-4 text-xs text-slate-500 dark:text-slate-400">
            <span className="inline-flex items-center gap-1">
              <ShieldCheck className="h-3.5 w-3.5 text-emerald-500" />
              SOC-2 Ready
            </span>
            <span>•</span>
            <span className="inline-flex items-center gap-1">
              <Zap className="h-3.5 w-3.5 text-amber-500" />
              Sub-second parsing
            </span>
            <span>•</span>
            <ThemeToggle showLabel={false} />
          </div>
        </div>

        <div className="mt-8 border-t border-slate-100 pt-6 text-center text-xs text-slate-400 dark:border-slate-800 dark:text-slate-500">
          © {new Date().getFullYear()} GrowEasy Technologies. Compatible with all GrowEasy CRM schemas and spreadsheets.
        </div>
      </div>
    </footer>
  );
}
