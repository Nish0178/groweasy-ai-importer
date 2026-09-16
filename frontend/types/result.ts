import type { CrmRecord } from "./crm";

export interface ImportErrorItem {
  original_row: number;
  record_identifier: string;
  error_type: string;
  error_message: string;
  recommended_action: string;
}

export interface ImportResult {
  success: boolean;
  totalImported: number;
  totalSkipped: number;
  totalRecords?: number;
  processedRecords?: number;
  skippedRecords?: number;
  processingPercentage?: number;
  records: CrmRecord[];
  downloadUrl?: string;
  duplicateCount?: number;
  invalidEmailCount?: number;
  missingFieldCount?: number;
  averageQualityScore?: number;
  leadStatusDistribution?: Record<string, number>;
  dataQualitySummary?: {
    averageScore?: number;
    duplicates?: number;
    invalidEmails?: number;
    missingContactInfo?: number;
  };
  errors?: ImportErrorItem[];
}