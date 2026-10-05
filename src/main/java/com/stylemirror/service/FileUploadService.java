package com.stylemirror.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String uploadProductImage(MultipartFile file);
    String uploadTryOnOverlay(MultipartFile file);
    String upload3dModel(MultipartFile file);
}
