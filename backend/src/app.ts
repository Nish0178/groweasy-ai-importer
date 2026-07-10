import express, { Application, Request, Response } from "express";
import cors from "cors";

import importRoutes from "./routes/import.routes";

const app: Application = express();

app.disable("x-powered-by");

// =====================================
// Global Middlewares
// =====================================

app.use(
  cors({
    origin: "http://localhost:3000",
    credentials: true,
  })
);

app.use(express.json());

app.use(
  express.urlencoded({
    extended: true,
  })
);

// =====================================
// Health Check
// =====================================

app.get("/api/health", (req: Request, res: Response) => {
  res.status(200).json({
    success: true,
    message: "GrowEasy AI Importer Backend is running 🚀",
    timestamp: new Date().toISOString(),
  });
});

// =====================================
// API Routes
// =====================================

app.use("/api/import", importRoutes);

// =====================================
// 404 Handler
// =====================================

app.use((req: Request, res: Response) => {
  res.status(404).json({
    success: false,
    message: "Route Not Found",
  });
});

export default app;