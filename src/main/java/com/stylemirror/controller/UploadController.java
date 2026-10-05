package com.stylemirror.controller;

import com.stylemirror.dto.ApiResponse;
import com.stylemirror.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/product-image")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProductImage(@RequestParam("file") MultipartFile file) {
        String path = fileUploadService.uploadProductImage(file);
        return ResponseEntity.ok(ApiResponse.ok("Product image uploaded successfully", Map.of("url", path)));
    }

    @PostMapping("/tryon-overlay")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadTryOnOverlay(@RequestParam("file") MultipartFile file) {
        String path = fileUploadService.uploadTryOnOverlay(file);
        return ResponseEntity.ok(ApiResponse.ok("Try-on overlay uploaded successfully", Map.of("url", path)));
    }

    @PostMapping("/avatar")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> upload3dModel(@RequestParam("file") MultipartFile file) {
        String path = fileUploadService.upload3dModel(file);
        return ResponseEntity.ok(ApiResponse.ok("3D Model uploaded successfully", Map.of("url", path)));
    }
}
