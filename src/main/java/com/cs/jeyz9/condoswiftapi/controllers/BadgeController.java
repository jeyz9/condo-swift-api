package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.dto.BadgeDTO;
import com.cs.jeyz9.condoswiftapi.dto.TableResponse;
import com.cs.jeyz9.condoswiftapi.services.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/badges")
public class BadgeController {
    private final BadgeService badgeService;

    @Autowired
    public BadgeController(BadgeService badgeService) {
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
    public ResponseEntity<String> updatedBadge(@PathVariable Long id, @RequestBody BadgeDTO request) {
        String response = badgeService.updatedBadge(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/deletedBadge/{id}")
    public ResponseEntity<String> deletedBadge(@PathVariable Long id) {
        String response = badgeService.deletedBadge(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/showAllBadges")
    public ResponseEntity<List<BadgeDTO>> showAllBadges() {
        return ResponseEntity.ok().body(badgeService.getAllBadges());
    }

    @PostMapping("/addAnnounceBadge")
    public ResponseEntity<String> addAnnounceBadge(@RequestParam Long announceId, @RequestParam Long badgeId){
        return new ResponseEntity<>(badgeService.addAnnounceBadge(announceId, badgeId), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/deleteBadgeFromAnnounce")
    public ResponseEntity<String> deleteBadgeFromAnnounce(@RequestParam Long announceId, @RequestParam Long badgeId) {
        return new ResponseEntity<>(badgeService.removeBadgeFromAnnounce(announceId, badgeId), HttpStatus.OK);
    }
}
