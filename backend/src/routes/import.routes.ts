import { Router } from "express";
import { upload } from "../middleware/upload.middleware";
import { uploadCSV } from "../controllers/import.controller";

const router = Router();

router.post(
  "/upload",
  upload.single("file"),
  uploadCSV
);

export default router;