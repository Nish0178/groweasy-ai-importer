import { GoogleGenAI } from "@google/genai";
import dotenv from "dotenv";

import { CRM_EXTRACTION_PROMPT } from "../prompts/crmExtraction.prompt";
import { AiExtractionResponse } from "../types/crm.types";

dotenv.config();

const apiKey = process.env.GEMINI_API_KEY;
if (!apiKey) {
  console.warn("⚠️ Warning: GEMINI_API_KEY is not set in environment variables.");
}

const ai = new GoogleGenAI({
  apiKey: apiKey || "",
});

export async function extractCrmRecords(
  records: unknown[]
): Promise<AiExtractionResponse> {
  if (!apiKey) {
    throw new Error("GEMINI_API_KEY is missing. Please check your environment configuration.");
  }

  const prompt = `
${CRM_EXTRACTION_PROMPT}

CSV Records:

${JSON.stringify(records, null, 2)}
`;

  try {
    const response = await ai.models.generateContent({
      model: "gemini-2.5-flash",
      contents: prompt,
    });

    let text = response.text ?? "";

    // Sanitize markdown fences
    text = text
      .replace(/```json/gi, "")
      .replace(/```/g, "")
      .trim();

    // Extract outer JSON boundaries
    const start = text.indexOf("{");
    const end = text.lastIndexOf("}");

    if (start === -1 || end === -1 || end <= start) {
      throw new Error("Gemini did not return a valid JSON object.");
    }

    const jsonText = text.substring(start, end + 1);
    const parsed = JSON.parse(jsonText) as AiExtractionResponse;

    if (!Array.isArray(parsed.records)) {
      throw new Error("Invalid AI response: 'records' field must be an array.");
    }

    if (typeof parsed.skipped !== "number") {
      parsed.skipped = 0;
    }

    return parsed;
  } catch (error) {
    const detail = error instanceof Error ? error.message : "Unknown AI error";
    throw new Error(`Failed to extract CRM records: ${detail}`);
  }
}