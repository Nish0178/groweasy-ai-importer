"use client";

import { useState, useMemo } from "react";
import { Search, Table as TableIcon } from "lucide-react";
import type { CsvRow } from "@/types/csv";

interface Props {
  readonly rows: CsvRow[];
}

export default function PreviewTable({ rows }: Props) {
  const [searchTerm, setSearchTerm] = useState("");

  const headers = useMemo(() => {
    return rows.length > 0 ? Object.keys(rows[0]) : [];
  }, [rows]);

  const filteredRows = useMemo(() => {
    if (!rows.length) return [];
    if (!searchTerm.trim()) return rows;
    const term = searchTerm.toLowerCase();
    return rows.filter((row) =>
      headers.some((header) =>
        String(row[header] ?? "")
          .toLowerCase()
          .includes(term)
      )
    );
  }, [rows, headers, searchTerm]);

  if (!rows.length) return null;

  return (
    <div className="mt-8 rounded-2xl border border-slate-200/90 bg-white/95 shadow-sm overflow-hidden dark:border-slate-800 dark:bg-slate-900/90 transition-colors">
      {/* Header bar */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 border-b border-slate-200/80 bg-slate-50/80 px-5 py-3.5 dark:border-slate-800 dark:bg-slate-850/80">
        <div className="flex items-center gap-2">
          <TableIcon className="h-4 w-4 text-blue-600 dark:text-blue-400" />
          <h4 className="text-sm font-semibold text-slate-900 dark:text-white">
            CSV Raw Data Preview
          </h4>
          <span className="rounded-full bg-blue-100 px-2 py-0.5 text-xs font-semibold text-blue-700 dark:bg-blue-950 dark:text-blue-300">
            {rows.length} total rows
          </span>
        </div>

        {/* Quick search */}
        <div className="relative w-full sm:w-64">
          <Search className="absolute left-2.5 top-2.5 h-3.5 w-3.5 text-slate-400" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Filter preview rows..."
            className="w-full rounded-lg border border-slate-200 bg-white pl-8 pr-3 py-1.5 text-xs text-slate-900 placeholder-slate-400 focus:border-blue-500 focus:outline-hidden dark:border-slate-700 dark:bg-slate-800 dark:text-white dark:placeholder-slate-500"
          />
        </div>
      </div>

      {/* Table Container */}
      <div className="max-h-96 overflow-auto scrollbar-thin">
        <table className="min-w-full divide-y divide-slate-200 text-left dark:divide-slate-800">
          <thead className="sticky top-0 z-10 bg-slate-100/95 backdrop-blur-xs dark:bg-slate-800/95">
            <tr>
              <th className="w-12 px-4 py-3 text-center text-[11px] font-semibold text-slate-500 dark:text-slate-400">
                #
              </th>
              {headers.map((header) => (
                <th
                  key={header}
                  className="px-4 py-3 text-xs font-semibold uppercase tracking-wider text-slate-700 dark:text-slate-200"
                >
                  <div className="flex items-center gap-1.5">
                    <span>{header}</span>
                  </div>
                </th>
              ))}
            </tr>
          </thead>

          <tbody className="divide-y divide-slate-200/60 bg-white dark:divide-slate-800/60 dark:bg-slate-900">
            {filteredRows.slice(0, 15).map((row, index) => {
              const rowKey = `${index}-${headers
                .map((header) => String(row[header] ?? ""))
                .join("|")}`;

              return (
                <tr
                  key={rowKey}
                  className="transition hover:bg-slate-50/80 dark:hover:bg-slate-800/50"
                >
                  <td className="px-4 py-2.5 text-center text-xs text-slate-400 dark:text-slate-500 font-mono">
                    {index + 1}
                  </td>
                  {headers.map((header) => (
                    <td
                      key={header}
                      className="px-4 py-2.5 text-xs text-slate-700 dark:text-slate-300 max-w-xs truncate"
                      title={String(row[header] ?? "")}
                    >
                      {String(row[header] ?? "") || (
                        <span className="text-slate-400 dark:text-slate-600 italic">
                          (empty)
                        </span>
                      )}
                    </td>
                  ))}
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Table Footer */}
      <div className="flex items-center justify-between border-t border-slate-200/80 bg-slate-50/90 px-5 py-2.5 text-xs text-slate-500 dark:border-slate-800 dark:bg-slate-850/90 dark:text-slate-400">
        <span>
          Showing top {Math.min(filteredRows.length, 15)} of {filteredRows.length} rows {searchTerm && "(filtered)"}
        </span>
        <span className="font-mono text-[11px]">
          {headers.length} detected columns
        </span>
      </div>
    </div>
  );
}