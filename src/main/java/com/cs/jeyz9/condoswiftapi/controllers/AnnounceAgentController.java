package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.dto.AgentDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceByAgentRequestDTO;
import com.cs.jeyz9.condoswiftapi.dto.ApproveAgentRequestDTO;
import com.cs.jeyz9.condoswiftapi.dto.ShowAllManageAnnounceDTO;
import com.cs.jeyz9.condoswiftapi.services.AnnounceAgentService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/announceAgent")
public class AnnounceAgentController {
    private final AnnounceAgentService announceAgentService;
    
    @Autowired
    public AnnounceAgentController(AnnounceAgentService announceAgentService) {
        this.announceAgentService = announceAgentService;
    }
    
    @PostMapping("/requestToManageAnnounce/{announceId}")
    public ResponseEntity<String> requestToManageAnnounce(@PathVariable("announceId") Long announceId, Principal principal) {
        return new ResponseEntity<>(announceAgentService.requestToManageAnnounce(principal.getName(), announceId), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/cancelManageRequest/{announceAgentId}")
    public ResponseEntity<String> cancelManageRequest(@PathVariable("announceAgentId") Long announceAgentId, Principal principal) {
        return new ResponseEntity<>(announceAgentService.cancelManageRequest(principal.getName(), announceAgentId), HttpStatus.OK);
    }
    
    @GetMapping("/getMyManagedAnnounces")
    public ResponseEntity<List<ShowAllManageAnnounceDTO>> getMyManagedAnnounces(Principal principal) {
        return new ResponseEntity<>(announceAgentService.getMyManagedAnnounces(principal.getName()), HttpStatus.OK);
    }
    
    @PutMapping("/updateAnnounceByAgent/{announceAgentId}")
    public ResponseEntity<String> updateAnnounceByAgent(@PathVariable("announceAgentId") Long announceAgentId, @RequestPart AnnounceByAgentRequestDTO announce, @RequestPart("files") List<MultipartFile> files, Principal principal){
        return new ResponseEntity<>(announceAgentService.updateAnnounceByAgent(announceAgentId, announce, files, principal.getName()), HttpStatus.OK);
    }
    
    @GetMapping("/getAgentsByAnnounce/{announceId}")
    public ResponseEntity<List<AgentDTO>> getAgentsByAnnounce(@PathVariable("announceId") Long announceId, Principal principal) {
        return new ResponseEntity<>(announceAgentService.getAgentsByAnnounce(principal.getName(),announceId), HttpStatus.OK);
    }
    
    @PutMapping("/approveAgent/{announceAgentId}")
    public ResponseEntity<String> approveAgent(@PathVariable("announceAgentId") Long announceAgentId, @RequestBody ApproveAgentRequestDTO request, Principal principal) {
        return new ResponseEntity<>(announceAgentService.approveAgent(principal.getName(), announceAgentId, request), HttpStatus.OK);
    }

    @PutMapping("/revokeAgent/{announceAgentId}")
    public ResponseEntity<String> revokeAgent(@PathVariable("announceAgentId") Long announceAgentId, Principal principal) {
        return new ResponseEntity<>(announceAgentService.revokeAgent(principal.getName(), announceAgentId), HttpStatus.OK);
    }
    
    @PutMapping("/updateAgentPermission/{announceAgentId}")
    public ResponseEntity<String> updateAgentPermission(@PathVariable("announceAgentId") Long announceAgentId, @RequestBody ApproveAgentRequestDTO request, Principal principal) {
        return new ResponseEntity<>(announceAgentService.updateAgentPermission(principal.getName(), announceAgentId, request), HttpStatus.OK);
    }
}

