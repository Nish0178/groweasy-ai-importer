import express, { Application } from "express";
import cors from "cors";

const app: Application = express();

// ======================
// Global Middlewares
// ======================
app.use(cors());

app.use(express.json());

app.use(
  express.urlencoded({
    extended: true,
  })
);

// ======================
// Health Check Route
// ======================
app.get("/api/health", (req, res) => {
  res.status(200).json({
    success: true,
    message: "GrowEasy AI Importer Backend is running 🚀",
    timestamp: new Date().toISOString(),
  });
});

// ======================
// 404 Route
// ======================
app.use("*", (req, res) => {
  res.status(404).json({
    success: false,
    message: "Route Not Found",
  });
});

export default app;
