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
    console.log("========== GEMINI RAW RESPONSE ==========");
console.log(response.text);
console.log("=========================================");

    let text = response.text ?? "";

    // Remove markdown fences if Gemini returns them
    text = text
      .replace(/```json/gi, "")
      .replace(/```/g, "")
      .trim();

    // Extract JSON even if Gemini adds extra text
    const start = text.indexOf("{");
    const end = text.lastIndexOf("}");

    if (start === -1 || end === -1) {
      throw new Error("Gemini did not return valid JSON.");
    }

    const jsonText = text.substring(start, end + 1);

    const parsed = JSON.parse(jsonText) as AiExtractionResponse;

    // Basic validation
    if (!Array.isArray(parsed.records)) {
      throw new Error("Invalid AI response: records must be an array.");
    }

    if (typeof parsed.skipped !== "number") {
      parsed.skipped = 0;
    }

    return parsed;
  } catch (error) {
    console.error("Gemini Error:", error);

    throw new Error("Failed to extract CRM records.");
  }
}