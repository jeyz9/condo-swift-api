package com.cs.jeyz9.condoswiftapi.services;


import com.cs.jeyz9.condoswiftapi.dto.LoginDTO;
import com.cs.jeyz9.condoswiftapi.dto.RegisterDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;

public interface AuthService {
    String register(RegisterDTO register) throws WebException;
    String login(LoginDTO login) throws WebException;
}
