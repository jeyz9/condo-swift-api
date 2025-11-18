package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.dto.RecommendedAgenDTO;
import com.cs.jeyz9.condoswiftapi.dto.UserProfileOverviewDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }
    
    @PostMapping("/{userId}/acceptTerms")
    public ResponseEntity<String> acceptTerms(@PathVariable Long userId, HttpServletRequest request) throws WebException {
        return new ResponseEntity<>(userService.userTermsAcceptLog(userId, request), HttpStatus.CREATED);
    }
    
    @GetMapping("/showRecommendedAgents")
    public ResponseEntity<List<RecommendedAgenDTO>> showRecommendedAgents() {
        return new ResponseEntity<>(userService.showRecommendedAgents(), HttpStatus.OK);
    }
    
    @GetMapping(value = "/showUserProfileOverview/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfileOverviewDTO> showUserProfileOverview(@PathVariable Long userId, @RequestParam(defaultValue = "ขาย") String type){
        return new ResponseEntity<>(userService.userProfileOverview(userId, type), HttpStatus.OK);
    }
    
    
    @PostMapping(value = "/{userId}/uploadProfilePicture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProfile(@PathVariable Long userId, @RequestPart MultipartFile imageFile){
        userService.saveImages(userId, imageFile);
        return new ResponseEntity<>("Upload profile picture success.", HttpStatus.CREATED);
    };
    
    @DeleteMapping("/{userId}/deleteProfilePicture")
    public ResponseEntity<String> deletedProfilePicture(@PathVariable Long userId) {
        userService.deleteImage(userId);
        return new ResponseEntity<>("Deleted profile picture success", HttpStatus.OK);
    }
}
