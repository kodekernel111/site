package com.kodekernel.site.controller;

import com.kodekernel.site.dto.ImageUploadResponse;
import com.kodekernel.site.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
public class ImageUploadController {

    private final S3Service s3Service;

    private static final java.util.List<String> ALLOWED_CONTENT_TYPES = java.util.Arrays.asList(
            "image/jpeg", "image/png", "image/webp", "image/gif");

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('WRITER', 'ADMIN')")
    public ResponseEntity<?> uploadImage(
            @RequestParam("image") MultipartFile file,
            Authentication authentication) {
        
        try {
            log.info("Received image upload request from user: {}", authentication.getName());
            
            if (file.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.getContentType().toLowerCase())) {
                throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, WEBP, and GIF are allowed.");
            }

            log.info("File details - Name: {}, Size: {}, Type: {}", 
                    file.getOriginalFilename(), file.getSize(), file.getContentType());
            
            String userEmail = authentication.getName();
            String imageUrl = s3Service.uploadImage(file, userEmail);
            
            ImageUploadResponse response = ImageUploadResponse.builder()
                    .url(imageUrl)
                    .filename(file.getOriginalFilename())
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .build();
            
            log.info("Image uploaded successfully: {}", imageUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Validation error during image upload", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation Error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IOException e) {
            log.error("IO error during image upload", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Upload Failed");
            error.put("message", "Failed to read the uploaded file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            log.error("Unexpected error during image upload", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal Server Error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/image")
    @PreAuthorize("hasAnyRole('WRITER', 'ADMIN')")
    public ResponseEntity<Void> deleteImage(@RequestParam("url") String imageUrl) {
        try {
            s3Service.deleteImage(imageUrl);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> checkS3Health() {
        boolean accessible = s3Service.isBucketAccessible();
        if (accessible) {
            return ResponseEntity.ok("S3 bucket is accessible");
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("S3 bucket is not accessible");
        }
    }
}
