import express, { Application, Request, Response } from "express";
import cors from "cors";
import path from "node:path";

import importRoutes from "./routes/import.routes";

const app: Application = express();

app.disable("x-powered-by");

// =====================================
// Global Middlewares
// =====================================

const allowedOrigins = [
  "http://localhost:3000",
  "http://127.0.0.1:3000",
  process.env.FRONTEND_URL,
].filter(Boolean) as string[];

app.use(
  cors({
    origin: (origin, callback) => {
      if (
        !origin ||
        allowedOrigins.includes(origin) ||
        origin.startsWith("http://localhost:") ||
        origin.startsWith("http://127.0.0.1:") ||
        process.env.NODE_ENV !== "production"
      ) {
        callback(null, true);
      } else {
        callback(new Error("Not allowed by CORS"));
      }
    },
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
// Serve Generated Excel Files
// =====================================

app.use(
  "/exports",
  express.static(path.join(process.cwd(), "exports"))
);

// =====================================
// Health Check
// =====================================

app.get("/api/health", (_req: Request, res: Response) => {
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

app.use((_req: Request, res: Response) => {
  res.status(404).json({
    success: false,
    message: "Route Not Found",
  });
});

export default app;