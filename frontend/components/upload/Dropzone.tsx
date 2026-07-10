"use client";

import { useCallback } from "react";
import { useDropzone } from "react-dropzone";
import { UploadCloud } from "lucide-react";

interface DropzoneProps {
  onFileSelect: (file: File) => void;
}

export default function Dropzone({
  onFileSelect,
}: DropzoneProps) {
  const onDrop = useCallback(
    (acceptedFiles: File[]) => {
      if (acceptedFiles.length > 0) {
        onFileSelect(acceptedFiles[0]);
      }
    },
    [onFileSelect]
  );

  const {
    getRootProps,
    getInputProps,
    isDragActive,
  } = useDropzone({
    accept: {
      "text/csv": [".csv"],
    },
    multiple: false,
    onDrop,
  });

  return (
    <div
      {...getRootProps()}
      className={`cursor-pointer rounded-2xl border-2 border-dashed p-16 text-center transition-all ${
        isDragActive
          ? "border-blue-600 bg-blue-100"
          : "border-blue-300 bg-blue-50"
      }`}
    >
      <input {...getInputProps()} />

      <UploadCloud className="mx-auto h-14 w-14 text-blue-600" />

      <h2 className="mt-6 text-2xl font-bold">
        Drag & Drop your CSV
      </h2>

      <p className="mt-2 text-gray-600">
        or click anywhere to browse
      </p>
    </div>
  );
}