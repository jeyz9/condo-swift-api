package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.RecommendedAgenDTO;
import com.cs.jeyz9.condoswiftapi.dto.UserProfileOverviewDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    String userTermsAcceptLog(Long userId, HttpServletRequest request) throws WebException;
    List<RecommendedAgenDTO> showRecommendedAgents();
    void deleteImage(Long userId);
    void saveImages(Long userId, MultipartFile imageFiles);
    UserProfileOverviewDTO userProfileOverview(Long userId, String saleType);
}
