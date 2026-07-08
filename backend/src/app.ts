import express, { Application, Request, Response } from "express";
import cors from "cors";

import importRoutes from "./routes/import.routes";

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
app.get("/api/health", (req: Request, res: Response) => {
  res.status(200).json({
    success: true,
    message: "GrowEasy AI Importer Backend is running 🚀",
    timestamp: new Date().toISOString(),
  });
});

// ======================
// Import Routes
// ======================
app.use("/api/import", importRoutes);

// ======================
// 404 Route
// ======================
app.use((req: Request, res: Response) => {
  res.status(404).json({
    success: false,
    message: "Route Not Found",
  });
});

export default app;