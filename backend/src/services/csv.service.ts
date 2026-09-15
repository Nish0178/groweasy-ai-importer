import { extractCrmRecords } from "./gemini.service";
import { AiExtractionResponse, CrmRecord } from "../types/crm.types";

const BATCH_SIZE = 20;

export async function processCsv(
  records: Record<string, any>[]
): Promise<AiExtractionResponse> {
  if (!records || records.length === 0) {
    return {
      records: [],
      skipped: 0,
    };
  }

  const finalRecords: CrmRecord[] = [];
  let skipped = 0;

  for (let i = 0; i < records.length; i += BATCH_SIZE) {
    const batch = records.slice(i, i + BATCH_SIZE);

    try {
      const result = await extractCrmRecords(batch);
      finalRecords.push(...result.records);
      skipped += result.skipped;
    } catch (error) {
      console.error(
        `Batch ${Math.floor(i / BATCH_SIZE) + 1} processing error:`,
        error instanceof Error ? error.message : error
      );
      skipped += batch.length;
    }
  }

  return {
    records: finalRecords,
    skipped,
  };
}