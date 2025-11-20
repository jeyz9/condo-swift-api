package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.config.JwtTokenProvider;
import com.cs.jeyz9.condoswiftapi.dto.ChangePasswordDTO;
import com.cs.jeyz9.condoswiftapi.dto.JwtAuthResponse;
import com.cs.jeyz9.condoswiftapi.dto.LoginDTO;
import com.cs.jeyz9.condoswiftapi.dto.OtpRequest;
import com.cs.jeyz9.condoswiftapi.dto.OtpResponse;
import com.cs.jeyz9.condoswiftapi.dto.RegisterDTO;
import com.cs.jeyz9.condoswiftapi.dto.ResetPasswordDTO;
import com.cs.jeyz9.condoswiftapi.dto.VerifyOtpRequest;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.services.AuthService;
import com.cs.jeyz9.condoswiftapi.services.ThaiBulkSmsService;
import com.cs.jeyz9.condoswiftapi.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ThaiBulkSmsService thaiBulkSmsService;

    @Autowired
    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider, ThaiBulkSmsService thaiBulkSmsService) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.thaiBulkSmsService = thaiBulkSmsService;
    }
    
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDTO registerDTO, HttpServletRequest request) throws WebException {
        String user = authService.register(registerDTO, request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginDTO loginDTO) throws WebException {
        String token = authService.login(loginDTO);
        Long userId = jwtTokenProvider.getUserId(token);

        ResponseCookie jwtCookie = ResponseCookie.from("token", token)
                .httpOnly(true)      
                .secure(true)       
                .path("/")         
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("None")
                .build();

        JwtAuthResponse response = new JwtAuthResponse();
        response.setUserId(userId);
        response.setAccessToken(token);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        ResponseCookie clearCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body("Logout success");
    }
    
    @PostMapping("/send-verify")
    public ResponseEntity<String> sendVerificationEmail(@RequestParam Long userId) {
        return ResponseEntity.ok(thaiBulkSmsService.verifyEmail(userId));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        String msg = authService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", msg));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<OtpResponse> requestOtp(@RequestParam Long userId) throws JsonProcessingException {
        return new ResponseEntity<>(thaiBulkSmsService.requestOtp(userId), HttpStatus.CREATED);
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String token, @RequestParam String otpCode) {
         String result = thaiBulkSmsService.verifyOtp(token, otpCode);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDTO req, BindingResult response, Principal principal, HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            token = bearer.substring(7);
        }
        
        if(response.hasErrors()){
            return ResponseEntity.badRequest().body(response.getAllErrors().toString());
        }
        
        return ResponseEntity.ok(authService.changePassword(principal.getName(), req, token));
    }
    
    @PostMapping("/sendEmailResetPassword")
    public ResponseEntity<String> sendEmailResetPassword(@RequestParam String email){
        return new ResponseEntity<>(thaiBulkSmsService.sendEmailResetPassword(email), HttpStatus.CREATED);
    }
    
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody ResetPasswordDTO request) {
        return new ResponseEntity<>(authService.resetPassword(token, request), HttpStatus.CREATED);
    }

}
