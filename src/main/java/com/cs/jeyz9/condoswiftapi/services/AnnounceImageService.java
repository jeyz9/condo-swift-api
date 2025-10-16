package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Announce;
import com.cs.jeyz9.condoswiftapi.models.AnnounceImage;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnounceImageService {

    private final AnnounceImageRepository announceImageRepository;

    @Value("${supabase.url}")
    private String supabaseUrl;
    
    @Value("${supabase.key}")
    private String supabaseKey;
    
    @Value("${supabase.bucket.announce}")
    private String bucket;

    public AnnounceImageService(AnnounceImageRepository announceImageRepository) {
        this.announceImageRepository = announceImageRepository;
    }

    public void saveImages(List<MultipartFile> imageFiles, Announce announce) {
        if (imageFiles == null || imageFiles.isEmpty()) return;
        if (imageFiles.size() > 5) {
            throw new WebException(HttpStatus.BAD_REQUEST, "You can upload max 5 images");
        }
        
        if(announce.getImageList().size() >= 5){
            throw new WebException(HttpStatus.BAD_REQUEST, "You can upload max 5 images");
        }

        RestTemplate restTemplate = new RestTemplate();

        for (MultipartFile file : imageFiles) {
            try {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.set("apikey", supabaseKey);
                headers.set("Authorization", "Bearer " + supabaseKey);

                HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

                String url = supabaseUrl + "/object/" + bucket + "/" + fileName;
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image: " + fileName);
                }

                AnnounceImage image = new AnnounceImage();
                image.setImageName(fileName);
                image.setAnnounce(announce);
                image.setCreatedAt(LocalDateTime.now());
                image.setExpireDate(LocalDateTime.now().plusMonths(3));
                image.setImageUrl(supabaseUrl + "/object/public/" + bucket + "/" + fileName);

                announceImageRepository.save(image);

            } catch (Exception e) {
                throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to store image: " + file.getOriginalFilename());
            }
        }
    }

//    public List<AnnounceImage> getImagesByAnnounce(Long announceId) {
//        return announceImageRepository.findByAnnounceId(announceId);
//    }

    @Transactional
    public void deleteImageById(Long announceImageId) {
        AnnounceImage img = announceImageRepository.findById(announceImageId)
                .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Image not found"));

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", supabaseKey);
            headers.set("Authorization", "Bearer " + supabaseKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String url = supabaseUrl + "/object/" + bucket + "/" + img.getImageName();

            restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete image: " + img.getImageName());
        }

        announceImageRepository.delete(img);
    }


    @Transactional
    public void deleteImagesByAnnounce(Long announceId) {
        List<AnnounceImage> images = announceImageRepository.findByAnnounceId(announceId);

        for (AnnounceImage img : images) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("apikey", supabaseKey);
                headers.set("Authorization", "Bearer " + supabaseKey);

                HttpEntity<Void> entity = new HttpEntity<>(headers);
                String url = supabaseUrl + "/object/" + bucket + "/" + img.getImageName();
                restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

            } catch (Exception e) {
                throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to delete image: " + img.getImageName());
            }

            announceImageRepository.delete(img);
        }
    }
}
