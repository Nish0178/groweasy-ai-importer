const BASE_API_URL =
  process.env.NEXT_PUBLIC_API_URL?.replace(/\/+$/, "") ||
  "https://groweasy-ai-importer-lctr.onrender.com";
const UPLOAD_URL = BASE_API_URL.endsWith("/api/import")
  ? `${BASE_API_URL}/upload`
  : `${BASE_API_URL}/api/import/upload`;

export async function uploadCsv(file: File) {
  const formData = new FormData();
  formData.append("file", file);

  let response: Response;
  try {
    response = await fetch(UPLOAD_URL, {
      method: "POST",
      body: formData,
    });
  } catch (error) {
    throw new Error(
      `Unable to connect to the backend server (${UPLOAD_URL}). Please verify that the backend server is running.`
    );
  }

  let data;
  try {
    data = await response.json();
  } catch (jsonError) {
    throw new Error(
      `Server returned status ${response.status} (${response.statusText}), but the response was not valid JSON.`
    );
  }

  if (!response.ok) {
    throw new Error(data?.message || "Failed to upload and process CSV.");
  }

  return data;
}