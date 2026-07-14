export interface ImportResult {
  success: boolean;
  totalImported: number;
  totalSkipped: number;
  records: any[];
  downloadUrl?: string;
}