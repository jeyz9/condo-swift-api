package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.dto.StationsDTO;
import com.cs.jeyz9.condoswiftapi.models.AnnounceType;
import com.cs.jeyz9.condoswiftapi.models.Province;
import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.services.SelectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/selector")
public class SelectorController {

    private final SelectorService selectorService;

    @Autowired
    public SelectorController(SelectorService selectorService) {
        this.selectorService = selectorService;
    }

    @GetMapping("/showAllProvinces")
    public ResponseEntity<List<Province>> showAllProvinces(){
        return ResponseEntity.ok().body(selectorService.getAllProvince());
    }
    
    @GetMapping("/showAllStations")
    public ResponseEntity<List<StationsDTO>> showAllStations() {
        return ResponseEntity.ok(selectorService.showALlStation());
    }
    
    @GetMapping("/showAllAnnounceTypes")
    public ResponseEntity<List<AnnounceType>> showAllAnnounceTypes() {
        return ResponseEntity.ok(selectorService.showAllAnnounceType());
    }
    
    @GetMapping("/showAllRoles")
    public ResponseEntity<List<Role>> showAllRoles(){
        return ResponseEntity.ok(selectorService.showAllRole());
    }
}
