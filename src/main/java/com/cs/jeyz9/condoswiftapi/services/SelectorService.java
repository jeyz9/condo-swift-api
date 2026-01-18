package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.RecipientDTO;
import com.cs.jeyz9.condoswiftapi.dto.StationsDTO;
import com.cs.jeyz9.condoswiftapi.models.AnnounceType;
import com.cs.jeyz9.condoswiftapi.models.Province;
import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.models.Station;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.ProvinceRepository;
import com.cs.jeyz9.condoswiftapi.repository.RoleRepository;
import com.cs.jeyz9.condoswiftapi.repository.StationRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelectorService {
    private final ProvinceRepository provinceRepository;
    private final StationRepository stationRepository;
    private final AnnounceTypeRepository announceTypeRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Autowired
    public SelectorService(ProvinceRepository provinceRepository, StationRepository stationRepository, AnnounceTypeRepository announceTypeRepository, RoleRepository roleRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.provinceRepository = provinceRepository;
        this.stationRepository = stationRepository;
        this.announceTypeRepository = announceTypeRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
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
    
    public List<Role> showAllRole() {
        return roleRepository.findAll();
    }
    
    public List<RecipientDTO> showAllRecipients() {
        return mapToRecipientDTO(userRepository.findAll());
    }
    
    public List<RecipientDTO> mapToRecipientDTO(List<User> users) {
        return users.stream().map(user -> modelMapper.map(user, RecipientDTO.class)).toList();
    }
}
