package org.example.eliteback.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:${java.io.tmpdir}/eliteback-uploads}")
    private String uploadDir;

    @Value("${app.upload.allowed-types:image/jpeg,image/png,image/webp}")
    private String allowedTypesConfig;

    @Value("${app.upload.max-size-bytes:5242880}")
    private long maxSizeBytes;

    public String store(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String contentType = file.getContentType();
        Set<String> allowed = Set.of(allowedTypesConfig.split(","));
        if (contentType == null || !allowed.contains(contentType.trim())) {
            throw new IllegalArgumentException("Invalid file type. Allowed: " + allowedTypesConfig);
        }
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException("File size exceeds limit");
        }
        Path root = Paths.get(uploadDir).resolve("user-" + userId);
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
        String ext = contentType.contains("png") ? ".png" : contentType.contains("webp") ? ".webp" : ".jpg";
        String filename = UUID.randomUUID() + ext;
        Path target = root.resolve(filename);
        try {
            file.transferTo(target.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
        return "/uploads/user-" + userId + "/" + filename;
    }
}
