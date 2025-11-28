package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.models.Province;
import com.cs.jeyz9.condoswiftapi.services.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/provinces")
public class ProvinceController {

    private final ProvinceService provinceService;

    @Autowired
    public ProvinceController(ProvinceService provinceService) {
        this.provinceService = provinceService;
    }

    @GetMapping("/showAllProvinces")
    public ResponseEntity<List<Province>> showAllProvinces(){
        return ResponseEntity.ok().body(provinceService.getAllProvince());
    }
}
