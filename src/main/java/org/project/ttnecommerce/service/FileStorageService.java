package org.project.ttnecommerce.service;
import org.project.ttnecommerce.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload.base-path}")
    private String basePath;

    public String storeProductVariationImage(
            MultipartFile file,
            UUID productId,
            UUID variationId,
            boolean isPrimary,
            int index
    ) {

        validateFile(file);

        try {
            String extension = getExtension(file.getOriginalFilename());

            String fileName = isPrimary
                    ? variationId + "." + extension
                    : variationId + "_" + index + "." + extension;

            Path uploadPath = Path.of(
                    basePath,
                    "products",
                    productId.toString(),
                    "variations"
            );

            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store image");
        }
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("File cannot be empty");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new InvalidRequestException("File size must be less than 5MB");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/jpg"))) {

            throw new InvalidRequestException("Invalid image format");
        }
    }

    private String getExtension(String fileName) {

        if (fileName == null || !fileName.contains(".")) {
            throw new InvalidRequestException("Invalid file name");
        }

        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}