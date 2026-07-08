import { Request, Response } from "express";

export const uploadCSV = (
  req: Request,
  res: Response
) => {
  if (!req.file) {
    return res.status(400).json({
      success: false,
      message: "No CSV uploaded",
    });
  }

  return res.status(200).json({
    success: true,
    message: "CSV uploaded successfully",
    file: req.file.filename,
  });
};