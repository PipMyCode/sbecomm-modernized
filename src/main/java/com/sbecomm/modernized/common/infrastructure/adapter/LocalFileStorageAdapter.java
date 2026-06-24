package com.sbecomm.modernized.common.infrastructure.adapter;

import com.sbecomm.modernized.common.application.port.FileStoragePort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    private final Path uploadDir = Paths.get("uploads");

    public LocalFileStorageAdapter() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty file.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String storedFilename = UUID.randomUUID() + extension;
        Path destinationFile = uploadDir.resolve(Paths.get(storedFilename)).normalize().toAbsolutePath();

        if (!destinationFile.getParent().equals(uploadDir.toAbsolutePath())) {
            throw new SecurityException("Cannot store file outside current directory.");
        }

        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        // Return a relative URL path that could be served by a static file handler
        return "/uploads/" + storedFilename;
    }
}
