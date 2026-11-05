package com.AgricultureFMSystem.AgricultureFMSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ============================================================================================
 * Phase 4: File Handling - File Upload Response DTO
 * ============================================================================================
 * // Purpose: Carries file metadata, storage location, size, and download link after upload
 * ============================================================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDTO {

    // // Generated unique file name on server disk
    private String fileName;

    // // Original client file name
    private String originalFileName;

    // // REST URL to download/stream the file
    private String fileDownloadUri;

    // // MIME content type (e.g. image/jpeg, application/pdf)
    private String fileType;

    // // File size in bytes
    private long size;

    // // Server upload timestamp
    private LocalDateTime uploadTimestamp;

    // // Status description message
    private String message;
}
