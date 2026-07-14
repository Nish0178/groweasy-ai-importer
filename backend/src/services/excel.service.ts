import * as XLSX from "xlsx";
import fs from "fs";
import path from "path";

export function generateExcel(records: any[]): string {
  // Create worksheet
  const worksheet = XLSX.utils.json_to_sheet(records);

  // Create workbook
  const workbook = XLSX.utils.book_new();

  XLSX.utils.book_append_sheet(workbook, worksheet, "CRM Records");

  // Ensure exports folder exists
  const exportDir = path.join(process.cwd(), "exports");

  if (!fs.existsSync(exportDir)) {
    fs.mkdirSync(exportDir, { recursive: true });
  }

  // File name
  const fileName = `crm_records_${Date.now()}.xlsx`;

  const filePath = path.join(exportDir, fileName);

  // Save Excel
  XLSX.writeFile(workbook, filePath);

  return fileName;
}