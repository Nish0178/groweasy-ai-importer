package com.groweasy.importer.controller;

import com.groweasy.importer.dto.ImportResponse;
import com.groweasy.importer.service.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private static final Logger log = LoggerFactory.getLogger(ImportController.class);

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ImportResponse> uploadCsv(
            @RequestParam(value = "file", required = false) MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ImportResponse.error("File is required. Supported file types are CSV and DOCX."));
        }

        try {
            log.info("Received upload request for file: {}", file.getOriginalFilename());
            ImportResponse response = importService.processUploadedFile(file);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Validation error processing upload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ImportResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Internal error processing upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ImportResponse.error(e.getMessage() != null ? e.getMessage() : "Failed to process file."));
        }
    }
}
