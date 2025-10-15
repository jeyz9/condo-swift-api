package com.cs.jeyz9.condoswiftapi.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

public class SupabaseStorageService {

    private final String SUPABASE_URL = "https://syrkqqdlwkpzpwuedspt.storage.supabase.co/storage/v1/s3";
    //    private final String SUPABASE_KEY = "82897e764c8e6dc74bf60a53cd11481d0a0fef509d800211c2b9eb056ee6e6de"; d0d0a606fc42e3857f6e1c81632889e4
    private final String SUPABASE_KEY = "d0d0a606fc42e3857f6e1c81632889e4"; 
    private final String BUCKET = "uploads";

    public String uploadFile(MultipartFile file) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("apikey", SUPABASE_KEY);
        headers.set("Authorization", "Bearer " + SUPABASE_KEY);

        HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

        String url = SUPABASE_URL + "/object/" + BUCKET + "/" + fileName;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return SUPABASE_URL + "/object/public/" + BUCKET + "/" + fileName;
        } else {
            throw new RuntimeException("Upload failed: " + response.getBody());
        }
    }
}
