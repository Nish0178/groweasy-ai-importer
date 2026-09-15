import multer from "multer";
import { Request } from "express";

const defaultMaxUploadSizeBytes = 10 * 1024 * 1024; // 10 MB

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

const storage = multer.memoryStorage();

const acceptedMimeTypes = new Set([
  "text/csv",
  "application/vnd.ms-excel",
  "application/csv",
  "text/plain",
  "text/comma-separated-values",
]);

const fileFilter: multer.Options["fileFilter"] = (
  _req: Request,
  file: Express.Multer.File,
  cb: multer.FileFilterCallback
) => {
  const isAcceptedMime = acceptedMimeTypes.has(file.mimetype);
  const isCsvExtension = file.originalname.toLowerCase().endsWith(".csv");

  if (isAcceptedMime || isCsvExtension) {
    cb(null, true);
  } else {
    cb(new Error("Only CSV files are allowed."));
  }
};

const upload = multer({
  storage,
  fileFilter,
  limits: {
    fileSize: maxUploadSizeBytes,
  },
});

export default upload;