import type { CrmRecord } from "./crm";

export interface ImportResult {
  success: boolean;
  totalImported: number;
  totalSkipped: number;
  records: CrmRecord[];
  downloadUrl?: string;
}