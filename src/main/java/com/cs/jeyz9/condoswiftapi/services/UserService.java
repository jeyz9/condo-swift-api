package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.RecommendedAgenDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {
    String userTermsAcceptLog(Long userId, HttpServletRequest request) throws WebException;
    List<RecommendedAgenDTO> showRecommendedAgents();
}
