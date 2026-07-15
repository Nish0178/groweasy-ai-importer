import { randomInt } from "node:crypto";
import fs from "node:fs";
import path from "node:path";
import multer from "multer";
import { Request } from "express";

// =======================================
// Ensure uploads directory exists
// =======================================

const uploadDir = "uploads";
const defaultMaxUploadSizeBytes = 10 * 1024 * 1024;
const maxUploadSizeBytes = (() => {
  const configuredSize = Number.parseInt(
    process.env.MAX_UPLOAD_SIZE ?? `${defaultMaxUploadSizeBytes}`,
    10
  );

  if (Number.isFinite(configuredSize) && configuredSize > 0) {
    return Math.min(configuredSize, defaultMaxUploadSizeBytes);
  }

  return defaultMaxUploadSizeBytes;
})();

if (!fs.existsSync(uploadDir)) {
  fs.mkdirSync(uploadDir, { recursive: true });
}

// =======================================
// Storage
// =======================================

const storage = multer.diskStorage({
  destination: (_req, _file, cb) => {
    cb(null, uploadDir);
  },

  filename: (_req, file, cb) => {
    const uniqueName = `${Date.now()}-${randomInt(1e9)}${path.extname(
      file.originalname
    )}`;

    cb(null, uniqueName);
  },
});

// =======================================
// File Filter
// =======================================

const fileFilter: multer.Options["fileFilter"] = (
  _req: Request,
  file: Express.Multer.File,
  cb: multer.FileFilterCallback
) => {
  const acceptedMimeTypes = new Set([
    "text/csv",
    "application/vnd.ms-excel",
    "application/csv",
    "text/plain",
    "text/comma-separated-values",
  ]);

  if (
    acceptedMimeTypes.has(file.mimetype) ||
    file.originalname.toLowerCase().endsWith(".csv")
  ) {
    cb(null, true);
  } else {
    cb(new Error("Only CSV files are allowed."));
  }
};

// =======================================
// Multer Instance
// =======================================

const upload = multer({
  storage,
  fileFilter,
  limits: {
    fileSize: maxUploadSizeBytes,
  },
});

export default upload;