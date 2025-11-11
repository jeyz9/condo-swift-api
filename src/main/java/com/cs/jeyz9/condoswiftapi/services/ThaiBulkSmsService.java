package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.OtpResponse;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Notification;
import com.cs.jeyz9.condoswiftapi.models.PasswordResetToken;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.models.VerificationOtpToken;
import com.cs.jeyz9.condoswiftapi.models.VerificationToken;
import com.cs.jeyz9.condoswiftapi.repository.NotificationRepository;
import com.cs.jeyz9.condoswiftapi.repository.PasswordResetTokenRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.repository.VerificationOtpTokenRepository;
import com.cs.jeyz9.condoswiftapi.repository.VerificationTokenRepository;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Service
public class ThaiBulkSmsService {

    private final UserRepository userRepository;
    private final VerificationOtpTokenRepository verificationOtpTokenRepository;
    private final NotificationRepository notificationRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${thaibulksms.key}")
    private String apiKey;

    @Value("${thaibulksms.secret}")
    private String apiSecret;

    @Value("${thaibulksms.request_url}")
    private String requestUrl;

    @Value("${thaibulksms.verify_url}")
    private String verifyUrl;
    
    @Value("${thaibulkemail.key}")
    private String emailApiKey;
    
    @Value("${thaibulkemail.secret}")
    private String emailApiSecret;

    @Value("${thaibulkemail.send_url}")
    private String sendUrl;

    @Value("${thaibulkemail.email_sender}")
    private String emailSender;

    @Value("${thaibulkemail.verify_production_url}")
    private String verifyEmailUrlEndpoint;

    @Value("${thaibulkemail.verify_email_template_uuid}")
    private String templateVerifyEmailUuid;
    
    @Value("${thaibulkemail.reset_password_url}")
    private String resetPasswordUrl;
    
    @Value("${thaibulkemail.reset_password_template_uuid}")
    private String templateResetPasswordUuid;

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    
    @Autowired
    public ThaiBulkSmsService(ObjectMapper mapper, RestTemplate restTemplate, UserRepository userRepository, VerificationOtpTokenRepository verificationOtpTokenRepository, NotificationRepository notificationRepository, VerificationTokenRepository tokenRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.verificationOtpTokenRepository = verificationOtpTokenRepository;
        this.notificationRepository = notificationRepository;
        this.tokenRepository = tokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public OtpResponse requestOtp(Long userId) throws JsonProcessingException {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found by phone number."));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String msisdn = user.getPhone().replaceAll("[\\s-]", "");
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
        }catch (HttpClientErrorException e) {
            if (e.getResponseBodyAsString().contains("ERROR_INSUFFICIENT_CREDIT")) {
                throw new WebException(HttpStatus.BAD_GATEWAY, "ThaiBulkSMS: เครดิตไม่เพียงพอ กรุณาเติมก่อนส่ง OTP");
            }
            throw e;
        }
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
            Notification notification = Notification
                    .builder()
                    .title("ยืนยันบอร์โทรศัพท์สำเร็จ")
                    .message("บัญชีของคุณได้รับการยืนยันบอร์โทรศัพท์เรียบร้อย")
                    .is_read(false)
                    .user(user)
                    .build();
            notificationRepository.save(notification);
            
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("ยืนยัน OTP ไม่สำเร็จ: " + e.getMessage(), e);
        }
    }
    
    public String verifyEmail(Long userId) {
        
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));

            if (user.getEmailVerified()) {
                throw new RuntimeException("บัญชีนี้ได้รับการยืนยันแล้ว");
            }

            String token = UUID.randomUUID().toString();

            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(user);
            verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
            tokenRepository.save(verificationToken);

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            header.setBasicAuth(emailApiKey, emailApiSecret);

            String body = "{"
                    + "\"mail_from\":{"
                    + "\"email\":\"" + emailSender + "\","
                    + "\"name\":\"Admin\""
                    + "},"
                    + "\"mail_to\":{"
                    + "\"email\":\"" + user.getEmail() + "\""
                    + "},"
                    + "\"payload\":{"
                    + "\"SERVER_URL\":\"" + verifyEmailUrlEndpoint + "\","
                    + "\"TOKEN\":\"" + token + "\""
                    + "},"
                    + "\"template_uuid\":\"" + templateVerifyEmailUuid + "\","
                    + "\"subject\":\"ยืนยันอีเมล\""
                    + "}";

            HttpEntity<String> requestEntity = new HttpEntity<>(body, header);

            ResponseEntity<String> response = restTemplate.exchange(
                    sendUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("ยืนยัน Email ไม่สำเร็จ: " + e.getMessage(), e);
        }
    }

    public String sendEmailResetPassword(String email) {

        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));

            String token = UUID.randomUUID().toString();
            PasswordResetToken reset = new PasswordResetToken();
            reset.setEmail(email);
            reset.setToken(token);
            reset.setExpiryDate(LocalDateTime.now().plusMinutes(15));
            passwordResetTokenRepository.save(reset);
            
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            header.setBasicAuth(emailApiKey, emailApiSecret);

            String body = "{"
                    + "\"mail_from\":{"
                    + "\"email\":\"" + emailSender + "\","
                    + "\"name\":\"Admin\""
                    + "},"
                    + "\"mail_to\":{"
                    + "\"email\":\"" + user.getEmail() + "\""
                    + "},"
                    + "\"payload\":{"
                    + "\"SERVER_URL\":\"" + resetPasswordUrl + "\","
                    + "\"TOKEN\":\"" + token + "\""
                    + "},"
                    + "\"template_uuid\":\"" + templateResetPasswordUuid + "\","
                    + "\"subject\":\"รีเซ็ตรหัสผ่าน\""
                    + "}";

            HttpEntity<String> requestEntity = new HttpEntity<>(body, header);

            ResponseEntity<String> response = restTemplate.exchange(
                    sendUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("รีเซ็ตรหัสผ่านไม่สำเร็จ: " + e.getMessage(), e);
        }
    }
}
