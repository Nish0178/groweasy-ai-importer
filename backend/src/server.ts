import dotenv from "dotenv";
import app from "./app";

dotenv.config();

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