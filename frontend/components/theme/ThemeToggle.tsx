"use client";

import React, { useEffect, useState } from "react";
import { Sun, Moon } from "lucide-react";
import { useTheme } from "./ThemeProvider";

interface ThemeToggleProps {
  className?: string;
  showLabel?: boolean;
}

export default function ThemeToggle({
  className = "",
  showLabel = true,
}: ThemeToggleProps) {
  const { resolvedTheme, setTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return (
      <div
        className={`inline-flex h-9 items-center rounded-xl border border-slate-200/80 bg-slate-100/70 p-1 text-slate-400 dark:border-slate-800 dark:bg-slate-800/70 ${className}`}
      >
        <div className="h-7 w-16" />
      </div>
    );
  }

  const isDark = resolvedTheme === "dark";

  return (
    <div
      className={`inline-flex items-center rounded-xl border border-slate-200/90 bg-slate-100/90 p-1 text-slate-600 shadow-2xs transition-colors dark:border-slate-800 dark:bg-slate-850 dark:text-slate-300 ${className}`}
      role="group"
      aria-label="Theme selection"
    >
      {/* Light Option Button */}
      <button
        type="button"
        onClick={() => setTheme("light")}
        className={`inline-flex items-center gap-1.5 rounded-lg px-2.5 py-1 text-xs font-semibold transition-all duration-200 ${
          !isDark
            ? "bg-white text-blue-600 shadow-xs ring-1 ring-slate-200/80 dark:bg-slate-750 dark:text-blue-400"
            : "text-slate-500 hover:text-slate-800 dark:text-slate-400 dark:hover:text-slate-200"
        }`}
        aria-pressed={!isDark}
        title="Switch to Light Theme"
      >
        <Sun className={`h-3.5 w-3.5 ${!isDark ? "text-amber-500" : "text-slate-400"}`} />
        {showLabel && <span>Light</span>}
      </button>

      {/* Dark Option Button */}
      <button
        type="button"
        onClick={() => setTheme("dark")}
        className={`inline-flex items-center gap-1.5 rounded-lg px-2.5 py-1 text-xs font-semibold transition-all duration-200 ${
          isDark
            ? "bg-slate-900 text-indigo-400 shadow-xs ring-1 ring-slate-700/80 dark:bg-slate-950 dark:text-indigo-300"
            : "text-slate-500 hover:text-slate-800 dark:text-slate-400 dark:hover:text-slate-200"
        }`}
        aria-pressed={isDark}
        title="Switch to Dark Theme"
      >
        <Moon className={`h-3.5 w-3.5 ${isDark ? "text-indigo-400" : "text-slate-400"}`} />
        {showLabel && <span>Dark</span>}
      </button>
    </div>
  );
}
