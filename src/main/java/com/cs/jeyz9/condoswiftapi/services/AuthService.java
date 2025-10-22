package com.cs.jeyz9.condoswiftapi.services;


import com.cs.jeyz9.condoswiftapi.dto.LoginDTO;
import com.cs.jeyz9.condoswiftapi.dto.RegisterDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    String register(RegisterDTO register, HttpServletRequest request) throws WebException;
    String login(LoginDTO login) throws WebException;
    void sendVerificationEmail(Long userId) throws MessagingException;
    String verifyEmail(String token);
}
