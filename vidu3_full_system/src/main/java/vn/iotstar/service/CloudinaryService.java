package vn.iotstar.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // Try uploading to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "iotstar_shop/" + folder,
                    "resource_type", "auto"
            ));
            String url = (String) uploadResult.get("secure_url");
            if (url != null) {
                log.info("Uploaded image to Cloudinary successfully: {}", url);
                return url;
            }
        } catch (Exception e) {
            log.warn("Cloudinary upload failed ({}), falling back to local storage...", e.getMessage());
        }

        // Local storage fallback
        try {
            Path uploadPath = Paths.get("./uploads", folder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String localUrl = "/uploads/" + folder + "/" + filename;
            log.info("Saved image to local storage: {}", localUrl);
            return localUrl;
        } catch (IOException e) {
            log.error("Failed to save image locally", e);
            return "/images/default-product.png";
        }
    }
}
