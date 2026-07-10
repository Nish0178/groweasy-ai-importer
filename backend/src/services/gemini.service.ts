import { GoogleGenAI } from "@google/genai";
import dotenv from "dotenv";

import { CRM_EXTRACTION_PROMPT } from "../prompts/crmExtraction.prompt";
import { AiExtractionResponse } from "../types/crm.types";

dotenv.config();

const ai = new GoogleGenAI({
  apiKey: process.env.GEMINI_API_KEY!,
});

export async function extractCrmRecords(
  records: unknown[]
): Promise<AiExtractionResponse> {
  try {
    const prompt = `
${CRM_EXTRACTION_PROMPT}

CSV Records:

${JSON.stringify(records, null, 2)}
`;

    const response = await ai.models.generateContent({
      model: "gemini-2.5-flash",
      contents: prompt,
    });

    const text =
      response.text?.trim().replaceAll("```json", "").replaceAll("```", "") ?? "";

    const parsed: AiExtractionResponse = JSON.parse(text);

    return parsed;
  } catch (error) {
    console.error("Gemini Error:", error);

    throw new Error("Failed to extract CRM records.");
  }
}