package com.cs.jeyz9.condoswiftapi.services;


import com.cs.jeyz9.condoswiftapi.dto.LoginDTO;
import com.cs.jeyz9.condoswiftapi.dto.RegisterDTO;

public interface AuthService {
    String register(RegisterDTO register);
    String login(LoginDTO login);
}
