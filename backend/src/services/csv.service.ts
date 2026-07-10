import { extractCrmRecords } from "./gemini.service";
import { AiExtractionResponse } from "../types/crm.types";

const BATCH_SIZE = 20;

export async function processCsv(
  records: any[]
): Promise<AiExtractionResponse> {
  let finalRecords: any[] = [];

  let skipped = 0;

  for (let i = 0; i < records.length; i += BATCH_SIZE) {
    const batch = records.slice(i, i + BATCH_SIZE);

    const result = await extractCrmRecords(batch);

    finalRecords.push(...result.records);

    skipped += result.skipped;
  }

  return {
    records: finalRecords,
    skipped,
  };
}