import dotenv from "dotenv";
dotenv.config();

import app from "./app";

const PORT = process.env.PORT || 5000;

app.listen(PORT, () => {
  console.log(`
==========================================
🚀 GrowEasy AI Importer Backend Started
==========================================
🌐 Server : http://localhost:${PORT}
❤️ Health : http://localhost:${PORT}/api/health
==========================================
`);
});