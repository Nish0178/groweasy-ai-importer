"use client";

import { useState } from "react";
import type { UploadedFile } from "@/types/upload";

export function useFileUpload() {
  const [selectedFile, setSelectedFile] =
    useState<UploadedFile | null>(null);

  const selectFile = (file: File) => {
    setSelectedFile({
      file,
      name: file.name,
      size: file.size,
      type: file.type,
    });
  };

  const removeFile = () => {
    setSelectedFile(null);
  };

  return {
    selectedFile,
    selectFile,
    removeFile,
  };
}
