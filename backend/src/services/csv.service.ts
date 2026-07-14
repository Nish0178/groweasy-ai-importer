import { extractCrmRecords } from "./gemini.service";
import { AiExtractionResponse } from "../types/crm.types";

const BATCH_SIZE = 20;

export async function processCsv(
  records: Record<string, any>[]
): Promise<AiExtractionResponse> {
  // Empty CSV
  if (!records || records.length === 0) {
    return {
      records: [],
      skipped: 0,
    };
  }

  const finalRecords: any[] = [];
  let skipped = 0;

  for (let i = 0; i < records.length; i += BATCH_SIZE) {
    const batch = records.slice(i, i + BATCH_SIZE);

    console.log(
      `Processing batch ${i / BATCH_SIZE + 1} (${batch.length} records)`
    );

    try {
      const result = await extractCrmRecords(batch);

      finalRecords.push(...result.records);

      skipped += result.skipped;
    } catch (error) {
      console.error("Batch Processing Failed:", error);

      skipped += batch.length;
    }
  }

  return {
    records: finalRecords,
    skipped,
  };
}