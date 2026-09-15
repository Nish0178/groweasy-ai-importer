import * as XLSX from "xlsx";
import fs from "node:fs";
import path from "node:path";
import { CrmRecord } from "../types/crm.types";

export function generateExcel(records: CrmRecord[]): string {
  const worksheet = XLSX.utils.json_to_sheet(records);
  const workbook = XLSX.utils.book_new();

  XLSX.utils.book_append_sheet(workbook, worksheet, "CRM Records");

  const exportDir = path.join(process.cwd(), "exports");
  if (!fs.existsSync(exportDir)) {
    fs.mkdirSync(exportDir, { recursive: true });
  }

  const fileName = `crm_records_${Date.now()}.xlsx`;
  const filePath = path.join(exportDir, fileName);

  XLSX.writeFile(workbook, filePath);

  return fileName;
}