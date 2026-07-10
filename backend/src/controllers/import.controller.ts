import { Request, Response } from "express";

export const uploadCSV = (
  req: Request,
  res: Response
): Response => {
  if (!req.file) {
    return res.status(400).json({
      success: false,
      message: "No CSV file uploaded.",
    });
  }

  return res.status(200).json({
    success: true,
    message: "CSV uploaded successfully.",
    file: {
      originalName: req.file.originalname,
      filename: req.file.filename,
      size: req.file.size,
      mimetype: req.file.mimetype,
    },
  });
};