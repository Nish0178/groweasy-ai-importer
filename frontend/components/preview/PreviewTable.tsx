"use client";

import type { CsvRow } from "@/types/csv";

interface Props {
  readonly rows: CsvRow[];
}

export default function PreviewTable({ rows }: Props) {
  if (!rows.length) return null;

  const headers = Object.keys(rows[0]);

  return (
    <div className="mt-10 overflow-auto rounded-2xl border">
      <table className="min-w-full border-collapse">
        <thead className="sticky top-0 bg-slate-100">
          <tr>
            {headers.map((header) => (
              <th
                key={header}
                className="border-b px-5 py-3 text-left font-semibold"
              >
                {header}
              </th>
            ))}
          </tr>
        </thead>

        <tbody>
          {rows.slice(0, 10).map((row, index) => {
            const rowKey = `${index}-${headers
              .map((header) => String(row[header] ?? ""))
              .join("|")}`;

            return (
              <tr key={rowKey} className="border-b">
                {headers.map((header) => (
                  <td key={header} className="px-5 py-3 text-sm">
                    {row[header]}
                  </td>
                ))}
              </tr>
            );
          })}
        </tbody>
      </table>

      <div className="border-t bg-slate-50 px-5 py-3 text-sm text-slate-600">
        Showing first {Math.min(rows.length, 10)} of {rows.length} rows
      </div>
    </div>
  );
}