package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.dto.RecommendedAgenDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }
    
    @PostMapping("/users/{userId}/accept-terms")
    public ResponseEntity<String> acceptTerms(@PathVariable Long userId, HttpServletRequest request) throws WebException {
        return new ResponseEntity<>(userService.userTermsAcceptLog(userId, request), HttpStatus.CREATED);
    }
    
    @GetMapping("/users/showRecommendedAgents")
    public ResponseEntity<List<RecommendedAgenDTO>> showRecommendedAgents() {
        return new ResponseEntity<>(userService.showRecommendedAgents(), HttpStatus.OK);
    } 
}
