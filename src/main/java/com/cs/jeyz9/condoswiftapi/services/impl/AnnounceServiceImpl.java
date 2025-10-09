package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.dto.AgenDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceByTypeDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDetailsSelected;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceImageDTO;
import com.cs.jeyz9.condoswiftapi.dto.NearbyPlaceAnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.RecommendAnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.ShowAnnounceWithCategoryResponse;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Announce;
import com.cs.jeyz9.condoswiftapi.models.AnnounceImage;
import com.cs.jeyz9.condoswiftapi.models.AnnounceStateApprove;
import com.cs.jeyz9.condoswiftapi.models.ApproveStatus;
import com.cs.jeyz9.condoswiftapi.models.Badge;
import com.cs.jeyz9.condoswiftapi.models.MapPoint;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceImageRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceStateApproveRepository;
import com.cs.jeyz9.condoswiftapi.repository.BadgeRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.services.AnnounceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AnnounceServiceImpl implements AnnounceService {
    private final AnnounceRepository announceRepository;
    private final UserRepository userRepository;
    private final AnnounceStateApproveRepository announceStateApproveRepository;
    private final BadgeRepository badgeRepository;
    private final AnnounceImageRepository announceImageRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AnnounceServiceImpl(AnnounceRepository announceRepository, UserRepository userRepository, AnnounceStateApproveRepository announceStateApproveRepository, BadgeRepository badgeRepository, AnnounceImageRepository announceImageRepository, ModelMapper modelMapper) {
        this.announceRepository = announceRepository;
        this.userRepository = userRepository;
        this.announceStateApproveRepository = announceStateApproveRepository;
        this.badgeRepository = badgeRepository;
        this.announceImageRepository = announceImageRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AnnounceDetailsSelected getAnnounceDetailsById(Long announceId) {
        Announce announce = announceRepository.findById(announceId)
                .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND,
                        "Announce not found with id: " + announceId));
        List<AnnounceImage> images = announceImageRepository.findByAnnounceId(announceId);
        List<AnnounceImageDTO> imageDTOS = images.stream().map(img -> modelMapper.map(img, AnnounceImageDTO.class)).toList();
        
        User user = userRepository.findById(announce.getUser().getId()).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not fond by id" + announce.getUser().getId()));
        AgenDTO agen = modelMapper.map(user, AgenDTO.class);
        agen.setIsVerify(user.getEmailVerified().equals(true) && user.getPhoneVerified().equals(true));
        
        AnnounceDetailsSelected announceDetailsSelected = mapToAnnounceDetailsSelected(announce);
        announceDetailsSelected.setImageList(imageDTOS);
        announceDetailsSelected.setAgen(agen);
        return announceDetailsSelected;
    }

    @Override
    public Announce getAnnounceById(Long announceId) {
        return announceRepository.findById(announceId)
                .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND,
                        "Announce not found with id: " + announceId));
    }
    
    @Override
    public AnnounceDTO addAnnounce(AnnounceDTO announceDTO) throws WebException {
        try{
            Announce announce = mapToAnnounce(announceDTO);
            announce.setId(null);
            announce.setTitle(announceDTO.getTitle());
            announce.setLocation(announceDTO.getLocation());
            announce.setPrice(announceDTO.getPrice());
            announce.setBathroomCount(announceDTO.getBathroomCount());
            announce.setBedroomCount(announceDTO.getBedroomCount());
            announce.setAreaSize(announceDTO.getAreaSize());
            announce.setHasPool(announceDTO.getHasPool());
            announce.setHasConvenienceStore(announceDTO.getHasConvenienceStore());
            announce.setHasFitness(announceDTO.getHasFitness());
            announce.setHasElevator(announceDTO.getHasElevator());
            announce.setHasParking(announceDTO.getHasParking());
            announce.setHasSecurity(announceDTO.getHasSecurity());
            User user = userRepository.findById(announceDTO.getUserId()).orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST, "User not found by id: " + announceDTO.getUserId()));
            announce.setUser(user);
            
            AnnounceStateApprove approveStatus = announceStateApproveRepository.findById(announceDTO.getApproveStatusId()).orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST, "Approve Status not found by id: " + announceDTO.getApproveStatusId()));
            if(!approveStatus.getStatusName().equals(ApproveStatus.DRAFT) && !approveStatus.getStatusName().equals(ApproveStatus.PENDING)){
                throw new WebException(HttpStatus.BAD_REQUEST, "Approve status must be DRAFT or PENDING only");
            }
            announce.setApprove(approveStatus);
            
            if(announceDTO.getBadges() != null){
                Set<Badge> badges = announceDTO.getBadges().stream()
                        .map(id -> badgeRepository.findById(id)
                                .orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST, "Badge not found id: " + id)))
                        .collect(Collectors.toSet());
                announce.setBadges(badges);
            }
    
            if (announceDTO.getMapPoints() != null) {
                announceDTO.getMapPoints().forEach(mpDTO -> {
                    MapPoint mapPoint = new MapPoint();
                    mapPoint.setLat(mpDTO.getLat());
                    mapPoint.setLng(mpDTO.getLng());
                    mapPoint.setAnnounce(announce);
                    announce.getMapPointList().add(mapPoint);
                });
            }
    
            announceRepository.save(announce);
            return mapToAnnounceDTO(announce);
            
        }catch (WebException e){
            throw e;
        }catch (Exception e){
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Added \"Announce\" fails " + e.getMessage());
        }
    }
    
    @Override
    public ShowAnnounceWithCategoryResponse showAnnounceWithCategory() throws WebException {
        try{
            ShowAnnounceWithCategoryResponse response = new ShowAnnounceWithCategoryResponse();
            List<Announce> announces = announceRepository.findAll();
            List<RecommendAnnounceDTO> recommendList = announces.stream()
                    .filter(announce -> announce.getBadges() != null && announce.getBadges().stream()
                            .anyMatch(badge -> badge != null && badge.getId() != null && badge.getId().equals(1L)))
                    .map(announce -> modelMapper.map(announce, RecommendAnnounceDTO.class)).limit(4)
                    .toList();
            
            List<NearbyPlaceAnnounceDTO> nearbyPlaceList = announces.stream()
                    .filter( (announce) ->
                            announce.getNearbyPlaces() != null && announce.getNearbyPlaces().stream()
                                    .anyMatch(nearbyPlace -> nearbyPlace != null && nearbyPlace.getId() != null && nearbyPlace.getId().equals(1L)))
                    .map(announce -> modelMapper.map(announce, NearbyPlaceAnnounceDTO.class)).limit(4)
                    .toList();
            
            List<AnnounceByTypeDTO> announceByTypeList = announces.stream()
                    .filter(announce -> announce.getAnnounceType() != null && announce.getAnnounceType().getId().equals(3L))
                    .map(announce -> modelMapper.map(announce, AnnounceByTypeDTO.class)).limit(4)
                    .toList();
            
            response.setRecommendAnnounces(recommendList);
            response.setNearbyPlaces(nearbyPlaceList);
            response.setLuxuryHouses(announceByTypeList);
            
            return response;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    };
    
    private Announce mapToAnnounce(AnnounceDTO announceDTO){
        return modelMapper.map(announceDTO, Announce.class);
    }
    
    private AnnounceDTO mapToAnnounceDTO(Announce announce) {
        return modelMapper.map(announce, AnnounceDTO.class);
    }
    
    private AnnounceDetailsSelected mapToAnnounceDetailsSelected (Announce announce) {
        return modelMapper.map(announce, AnnounceDetailsSelected.class);
    }
    
}
