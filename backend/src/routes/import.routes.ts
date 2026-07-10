import { Router } from "express";
import multer from "multer";

import upload from "../middleware/upload.middleware";
import { uploadCsvController } from "../controllers/import.controller";

const router = Router();

router.post(
  "/upload",
  upload.single("file"),
  uploadCsvController
);

export default router;