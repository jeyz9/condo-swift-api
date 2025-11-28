package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.models.Province;
import com.cs.jeyz9.condoswiftapi.repository.ProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinceService {
    private final ProvinceRepository provinceRepository;
    
    @Autowired
    public ProvinceService(ProvinceRepository provinceRepository) {
        this.provinceRepository = provinceRepository;
    }
    
    public List<Province> getAllProvince() {
        return provinceRepository.findAll();
    }
}
