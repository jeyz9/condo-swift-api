package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.UserTermsAcceptLog;
import com.cs.jeyz9.condoswiftapi.services.UserTermsAcceptLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {
    private final UserTermsAcceptLogService userTermsAcceptLogService;
    
    @Autowired
    public UserController(UserTermsAcceptLogService userTermsAcceptLogService){
        this.userTermsAcceptLogService = userTermsAcceptLogService;
    }
    
    @PostMapping("/users/{userId}/accept-terms")
    public ResponseEntity<String> acceptTerms(@PathVariable Long userId, HttpServletRequest request) throws WebException {
        return new ResponseEntity<>(userTermsAcceptLogService.userTermsAcceptLog(userId, request), HttpStatus.CREATED);
    }
}
