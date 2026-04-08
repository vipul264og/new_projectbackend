package com.edulib.config;

import com.edulib.exception.FileStorageException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Component
public class FileStorageConfig {

    private static final Logger log = LoggerFactory.getLogger(FileStorageConfig.class);

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Value("${app.file.allowed-types}")
    private String allowedTypes;

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        // Always resolve to an absolute, normalized path so the location is
        // identical regardless of which directory the JVM was launched from.
        // Using ${user.home}/edulib-uploads in application.yml guarantees this.
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException(
                    "Could not create upload directory: " + this.fileStorageLocation, ex);
        }

        // Print clearly so developers can immediately see where files will land
        log.info("==============================================");
        log.info("  FILE STORAGE LOCATION:");
        log.info("  {}", this.fileStorageLocation);
        log.info("==============================================");
    }

    public String storeFile(MultipartFile file) {
        validateFile(file);

        String originalFileName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension  = getFileExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + fileExtension;
        Path   targetLocation = this.fileStorageLocation.resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new FileStorageException(
                    "Could not store file '" + originalFileName + "': " + ex.getMessage(), ex);
        }

        log.info("File stored: original='{}' saved-as='{}' path='{}'",
                originalFileName, storedFileName, targetLocation);
        return storedFileName;
    }

    public Path loadFile(String storedFileName) {
        // Resolve against the absolute storage location — always consistent
        Path filePath = this.fileStorageLocation.resolve(storedFileName).normalize();

        // Guard against path traversal attacks (e.g. ../../etc/passwd)
        if (!filePath.startsWith(this.fileStorageLocation)) {
            throw new FileStorageException("Illegal file path: " + storedFileName);
        }

        if (!Files.exists(filePath)) {
            // Log the full absolute path to make debugging trivial
            log.error("File not found on disk. Expected at: {}", filePath);
            log.error("Storage root is: {}", this.fileStorageLocation);
            log.error("Stored filename from DB: {}", storedFileName);
            throw new FileStorageException(
                    "File not found: " + storedFileName +
                    " | Storage location: " + this.fileStorageLocation);
        }

        log.debug("File loaded: {}", filePath);
        return filePath;
    }

    public void deleteFile(String storedFileName) {
        Path filePath = this.fileStorageLocation.resolve(storedFileName).normalize();

        if (!filePath.startsWith(this.fileStorageLocation)) {
            throw new FileStorageException("Illegal file path: " + storedFileName);
        }

        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("File deleted: {}", filePath);
            } else {
                log.warn("File to delete was already missing: {}", filePath);
            }
        } catch (IOException ex) {
            log.error("Could not delete file: {}", filePath, ex);
            throw new FileStorageException("Could not delete file: " + storedFileName, ex);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File is empty or null");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals(allowedTypes)) {
            throw new FileStorageException(
                    "Only PDF files are allowed. Received: " + contentType);
        }

        String filename = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename()));
        if (filename.contains("..")) {
            throw new FileStorageException(
                    "Filename contains invalid path sequence: " + filename);
        }
    }

    private String getFileExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot >= 0) ? filename.substring(dot) : ".pdf";
    }

    public Path getStorageLocation() {
        return fileStorageLocation;
    }
}
