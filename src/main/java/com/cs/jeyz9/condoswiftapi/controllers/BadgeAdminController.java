package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.dto.BadgeDTO;
import com.cs.jeyz9.condoswiftapi.dto.TableResponse;
import com.cs.jeyz9.condoswiftapi.models.Badge;
import com.cs.jeyz9.condoswiftapi.services.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/badges")
public class BadgeAdminController {
    private final BadgeService badgeService;
    
    @Autowired
    public BadgeAdminController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }
    
    @GetMapping("/filterBadges")
    public ResponseEntity<TableResponse<BadgeDTO>> filterBadge(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) throws IOException {
        return new ResponseEntity<>(badgeService.showAllBadge(keyword, page, size), HttpStatus.OK);
    }
    
    @PostMapping("/addedBadge")
    public ResponseEntity<String> addedBadge(@RequestBody BadgeDTO request){
        String response = badgeService.addedBadge(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/updatedBadge/{id}")
    public ResponseEntity<String> updatedBadge(@RequestParam Long id, @RequestBody BadgeDTO request) {
        String response = badgeService.updatedBadge(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/deletedBadge/{id}")
    public ResponseEntity<String> deletedBadge(@RequestParam Long id) {
        String response = badgeService.deletedBadge(id);
        return ResponseEntity.ok(response);
    }
}
