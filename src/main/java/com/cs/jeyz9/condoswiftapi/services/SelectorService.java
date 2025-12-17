package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.StationsDTO;
import com.cs.jeyz9.condoswiftapi.models.AnnounceType;
import com.cs.jeyz9.condoswiftapi.models.Province;
import com.cs.jeyz9.condoswiftapi.models.Station;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.ProvinceRepository;
import com.cs.jeyz9.condoswiftapi.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelectorService {
    private final ProvinceRepository provinceRepository;
    private final StationRepository stationRepository;
    private final AnnounceTypeRepository announceTypeRepository;

    @Autowired
    public SelectorService(ProvinceRepository provinceRepository, StationRepository stationRepository, AnnounceTypeRepository announceTypeRepository) {
        this.provinceRepository = provinceRepository;
        this.stationRepository = stationRepository;
        this.announceTypeRepository = announceTypeRepository;
    }
    
    public List<Province> getAllProvince() {
        return provinceRepository.findAll();
    }
    
    public List<StationsDTO> showALlStation() {
        return mapToStationsDTO(stationRepository.findAll());
    }
    
    public List<StationsDTO> mapToStationsDTO (List<Station> stations) {
        return stations.stream().map(s -> 
            StationsDTO.builder()
                    .id(s.getId())
                    .name(s.getName())
                    .stationType(s.getStationType())
                    .build()
        ).toList();
    }
    
    public List<AnnounceType> showAllAnnounceType() {
        return announceTypeRepository.findAll();
    }
}
