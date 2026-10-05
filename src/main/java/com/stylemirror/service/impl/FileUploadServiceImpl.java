package com.stylemirror.service.impl;

import com.stylemirror.exception.BadRequestException;
import com.stylemirror.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${app.upload.dir:uploads}")
    private String baseUploadDir;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/webp", "image/svg+xml"
    );

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".webp", ".svg"
    );

    private static final List<String> ALLOWED_MODEL_EXTENSIONS = Arrays.asList(
            ".glb", ".gltf"
    );

    @Override
    public String uploadProductImage(MultipartFile file) {
        return storeFile(file, "products", ALLOWED_IMAGE_TYPES, ALLOWED_IMAGE_EXTENSIONS);
    }

    @Override
    public String uploadTryOnOverlay(MultipartFile file) {
        return storeFile(file, "tryon", ALLOWED_IMAGE_TYPES, ALLOWED_IMAGE_EXTENSIONS);
    }

    @Override
    public String upload3dModel(MultipartFile file) {
        return storeFile(file, "models", null, ALLOWED_MODEL_EXTENSIONS);
    }

    private String storeFile(MultipartFile file, String subfolder, List<String> allowedMimes, List<String> allowedExts) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file cannot be empty");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "file"));

        // Path traversal validation
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new BadRequestException("Filename contains invalid path characters");
        }

        // Extension extraction and validation
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        if (allowedExts != null && !allowedExts.contains(extension)) {
            throw new BadRequestException("File extension '" + extension + "' is not supported. Allowed: " + allowedExts);
        }

        // MIME type validation
        if (allowedMimes != null && file.getContentType() != null) {
            String mime = file.getContentType().toLowerCase();
            if (!allowedMimes.contains(mime)) {
                throw new BadRequestException("MIME type '" + mime + "' is not supported");
            }
        }

        try {
            Path targetDir = Paths.get(baseUploadDir, subfolder).toAbsolutePath().normalize();
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // Generate safe non-guessable random UUID filename
            String storedFileName = UUID.randomUUID().toString() + extension;
            Path targetLocation = targetDir.resolve(storedFileName).normalize();

            // Guard against directory escaping
            if (!targetLocation.startsWith(targetDir)) {
                throw new BadRequestException("Target location escapes upload directory");
            }

            try (InputStream is = file.getInputStream()) {
                Files.copy(is, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/" + subfolder + "/" + storedFileName;
        } catch (IOException ex) {
            throw new BadRequestException("Failed to store file: " + ex.getMessage());
        }
    }
}
