package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Announce;
import com.cs.jeyz9.condoswiftapi.models.AnnounceImage;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnounceImageService {
    private final AnnounceImageRepository announceImageRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Autowired
    public AnnounceImageService(AnnounceImageRepository announceImageRepository) {
        this.announceImageRepository = announceImageRepository;
    }

    public void saveImages(List<MultipartFile> imageFiles, Announce announce) {
        if (imageFiles == null || imageFiles.isEmpty()) return;
        
        if (imageFiles.size() > 5) {
            throw new WebException(HttpStatus.BAD_REQUEST, "You can upload max 5 images");
        }
        
        for (MultipartFile file : imageFiles) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            try {
                Path filePath = Path.of(uploadDir, fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                AnnounceImage image = new AnnounceImage();
                image.setImageName(fileName);
                image.setAnnounce(announce);
                image.setCreatedAt(LocalDateTime.now());
                image.setExpireDate(LocalDateTime.now().plusMonths(3));

                announceImageRepository.save(image);

            } catch (IOException e) {
                throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to store image: " + file.getOriginalFilename());
            }
        }
    }

    public List<AnnounceImage> getImagesByAnnounce(Long announceId) {
        return announceImageRepository.findByAnnounceId(announceId);
    }

    @Transactional
    public void deleteImagesByAnnounce(Long announceId) {
        List<AnnounceImage> images = announceImageRepository.findByAnnounceId(announceId);

        for (AnnounceImage img : images) {
            try {
                Path filePath = Path.of(uploadDir, img.getImageName());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to delete image file: " + img.getImageName());
            }

            announceImageRepository.delete(img);
        }
    }
}
