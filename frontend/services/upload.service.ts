import axios from "axios";

const API = "http://localhost:5000/api/import";

export async function uploadCSV(file: File) {
  const formData = new FormData();

  formData.append("file", file);

  const response = await axios.post(
    `${API}/upload`,
    formData
  );

  return response.data;
}
