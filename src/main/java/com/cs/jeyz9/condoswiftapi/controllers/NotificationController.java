package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.dto.NotificationDTO;
import com.cs.jeyz9.condoswiftapi.dto.SendNotificationDTO;
import com.cs.jeyz9.condoswiftapi.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    
    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @PostMapping("/sendNotification")
    public ResponseEntity<String> sendNotification(@RequestBody SendNotificationDTO sendNotify, Principal principal) {
        return new ResponseEntity<>(notificationService.sendNotification(principal.getName(), sendNotify), HttpStatus.CREATED);
    }
    
    @GetMapping("/showAllNotificationSelectedByUserId")
    public ResponseEntity<List<NotificationDTO>> showAllNotificationSelectedByUserId(Principal principal) {
        return new ResponseEntity<>(notificationService.showAllNotificationSelectedByUser(principal.getName()), HttpStatus.OK);
    }
    
    @GetMapping("/showNotificationDetailsSelected/{notifyId}")
    public ResponseEntity<NotificationDTO> showNotificationDetailsSelected(@PathVariable Long notifyId, Principal principal){
        return new ResponseEntity<>(notificationService.showNotificationDetailsSelected(principal.getName(), notifyId), HttpStatus.OK);
    }
    
    @DeleteMapping("/deleteNotification/{notifyId}")
    public ResponseEntity<String> deleteNotification(Long notifyId) {
        return new ResponseEntity<>(notificationService.deleteNotification(notifyId), HttpStatus.OK);
    }
    
}
