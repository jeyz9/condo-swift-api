package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.EditProfileDTO;
import com.cs.jeyz9.condoswiftapi.dto.RecommendedAgenDTO;
import com.cs.jeyz9.condoswiftapi.dto.ShowAllAnnounceDetailsWithAgent;
import com.cs.jeyz9.condoswiftapi.dto.ShowAllUserDTO;
import com.cs.jeyz9.condoswiftapi.dto.ShowUserDetailsDTO;
import com.cs.jeyz9.condoswiftapi.dto.TableResponse;
import com.cs.jeyz9.condoswiftapi.dto.UserProfileOverviewDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    String userTermsAcceptLog(String email, HttpServletRequest request) throws WebException;
    List<RecommendedAgenDTO> showRecommendedAgents();
    void deleteImage(String email);
    void saveImages(String email, MultipartFile imageFiles);
    UserProfileOverviewDTO userProfileOverview(Long userId, String saleType);
    String bookmarkAnnounce(String email, Long announceId);
    String removeFromBookmark(String email, Long announceId);
    List<ShowAllAnnounceDetailsWithAgent> showAllAnnounceBookmark(String email);
    String updateUserProfile(String email, EditProfileDTO editProfile);
    ShowUserDetailsDTO showUserDetails(String email);
    TableResponse<ShowAllUserDTO> showAllUser(String keyword, Integer page, Integer size) throws IOException;
    String addUserRole(Long userId, Long roleId);
    String deleteUserRole(Long userId, Long roleId);
}
