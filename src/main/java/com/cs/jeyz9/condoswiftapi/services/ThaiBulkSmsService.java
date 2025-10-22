package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.OtpResponse;
import com.cs.jeyz9.condoswiftapi.dto.VerifyOtpRequest;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.models.VerificationOtpToken;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.repository.VerificationOtpTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service
public class ThaiBulkSmsService {

    private final UserRepository userRepository;
    private final VerificationOtpTokenRepository verificationOtpTokenRepository;
    @Value("${thaibulksms.key}")
    private String apiKey;

    @Value("${thaibulksms.secret}")
    private String apiSecret;

    @Value("${thaibulksms.request_url}")
    private String requestUrl;

    @Value("${thaibulksms.verify_url}")
    private String verifyUrl;

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    
    @Autowired
    public ThaiBulkSmsService(ObjectMapper mapper, RestTemplate restTemplate, UserRepository userRepository, VerificationOtpTokenRepository verificationOtpTokenRepository) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.verificationOtpTokenRepository = verificationOtpTokenRepository;
    }

    public OtpResponse requestOtp(String msisdn) throws JsonProcessingException {

        User user = userRepository.findByPhone(msisdn).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found by phone number."));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        msisdn = msisdn.replaceAll("[\\s-]", "");
        if (msisdn.startsWith("0")) {
            msisdn = "+66" + msisdn.substring(1);
        }
        
        String cleanedMsisdn = msisdn.trim();
        String encodedMsisdn = URLEncoder.encode(cleanedMsisdn, StandardCharsets.UTF_8);
        String body = "key=" + apiKey +
                "&secret=" + apiSecret +
                "&msisdn=" + encodedMsisdn;

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        OtpResponse otpResponse = mapper.readValue(response.getBody(), OtpResponse.class);
        VerificationOtpToken otpToken = new VerificationOtpToken();
        otpToken.setToken(otpResponse.getToken());
        otpToken.setRefno(otpResponse.getRefno());
        otpToken.setUser(user);
        verificationOtpTokenRepository.save(otpToken);
        
        return otpResponse;
    }

    public String verifyOtp(String token, String otpCode) {
        try {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            VerificationOtpToken verificationToken = verificationOtpTokenRepository.findByToken(token).orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST, "Token ไม่ถูกต้อง"));
            String body = "key=" + apiKey +
                    "&secret=" + apiSecret +
                    "&token=" + token +
                    "&pin=" + otpCode;
            HttpEntity<String> requestEntity = new HttpEntity<>(body, header);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    verifyUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            
            User user = verificationToken.getUser();
            user.setPhoneVerified(true);
            userRepository.save(user);
            verificationOtpTokenRepository.delete(verificationToken);
            
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ยืนยัน OTP ไม่สำเร็จ: " + e.getMessage(), e);
        }
    }
}
