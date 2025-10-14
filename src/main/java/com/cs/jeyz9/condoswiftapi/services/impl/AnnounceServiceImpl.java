package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.dto.AgenDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceByTypeDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDetailsSelected;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceImageDTO;
import com.cs.jeyz9.condoswiftapi.dto.MapPointDTO;
import com.cs.jeyz9.condoswiftapi.dto.NearbyPlaceAnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.RecommendAnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.ShowAnnounceWithCategoryResponse;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Announce;
import com.cs.jeyz9.condoswiftapi.models.AnnounceImage;
import com.cs.jeyz9.condoswiftapi.models.AnnounceStateApprove;
import com.cs.jeyz9.condoswiftapi.models.AnnounceType;
import com.cs.jeyz9.condoswiftapi.models.ApproveStatus;
import com.cs.jeyz9.condoswiftapi.models.Badge;
import com.cs.jeyz9.condoswiftapi.models.MapPoint;
import com.cs.jeyz9.condoswiftapi.models.NearbyPlace;
import com.cs.jeyz9.condoswiftapi.models.NearbyPlaceTypes;
import com.cs.jeyz9.condoswiftapi.models.SaleType;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceImageRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceStateApproveRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.BadgeRepository;
import com.cs.jeyz9.condoswiftapi.repository.NearbyPlaceRepository;
import com.cs.jeyz9.condoswiftapi.repository.SaleTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.services.AnnounceImageService;
import com.cs.jeyz9.condoswiftapi.services.AnnounceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    private final AnnounceImageService announceImageService;
    private final NearbyPlaceRepository nearbyPlaceRepository;
    private final AnnounceTypeRepository announceTypeRepository;
    private final SaleTypeRepository saleTypeRepository;

    @Autowired
    public AnnounceServiceImpl(AnnounceRepository announceRepository, UserRepository userRepository, AnnounceStateApproveRepository announceStateApproveRepository, BadgeRepository badgeRepository, AnnounceImageRepository announceImageRepository, ModelMapper modelMapper, AnnounceImageService announceImageService, NearbyPlaceRepository nearbyPlaceRepository, AnnounceTypeRepository announceTypeRepository, SaleTypeRepository saleTypeRepository) {
        this.announceRepository = announceRepository;
        this.userRepository = userRepository;
        this.announceStateApproveRepository = announceStateApproveRepository;
        this.badgeRepository = badgeRepository;
        this.announceImageRepository = announceImageRepository;
        this.modelMapper = modelMapper;
        this.announceImageService = announceImageService;
        this.nearbyPlaceRepository = nearbyPlaceRepository;
        this.announceTypeRepository = announceTypeRepository;
        this.saleTypeRepository = saleTypeRepository;
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
            
            if (announceDTO.getBadges() != null && !announceDTO.getBadges().isEmpty()) {
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

            AnnounceType type = announceTypeRepository.findById(announceDTO.getAnnounceType()).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Type not found by id: " + announceDTO.getAnnounceType()));
            announce.setAnnounceType(type);

            SaleType saleType = saleTypeRepository.findById(announceDTO.getSaleType()).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Sale type not found by id: " + announceDTO.getSaleType()));
            announce.setSaleType(saleType);

            if (announceDTO.getNearbyPlaces() != null && !announceDTO.getNearbyPlaces().isEmpty()) {
                Set<NearbyPlace> nearbyPlaces = announceDTO.getNearbyPlaces().stream()
                        .map(id -> nearbyPlaceRepository.findById(id)
                                .orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST, "NearbyPlace not found id: " + id)))
                        .collect(Collectors.toSet());
                announce.setNearbyPlaces(nearbyPlaces);
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
    public AnnounceDTO editAnnounce(Long announceId, AnnounceDTO announceDTO) throws WebException {
        try{
            Announce announce = announceRepository.findById(announceId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce not found by id: " + announceId));
            User user = userRepository.findById(announceDTO.getUserId()).orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST, "User not found by id: " + announceDTO.getUserId()));
            if(!user.getId().equals(announce.getUser().getId())){
                throw new WebException(HttpStatus.BAD_REQUEST, "User not match");
            }
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
            announce.setUser(user);

            AnnounceStateApprove approveStatus = announceStateApproveRepository.findById(announceDTO.getApproveStatusId()).orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST, "Approve Status not found by id: " + announceDTO.getApproveStatusId()));
            if(!approveStatus.getStatusName().equals(ApproveStatus.DRAFT) && !approveStatus.getStatusName().equals(ApproveStatus.PENDING)){
                throw new WebException(HttpStatus.BAD_REQUEST, "Approve status must be DRAFT or PENDING only");
            }
            announce.setApprove(approveStatus);

            if(announceDTO.getBadges() != null){
                announce.getBadges().clear();
                Set<Badge> badges = announceDTO.getBadges().stream()
                        .map(id -> badgeRepository.findById(id)
                                .orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST, "Badge not found id: " + id)))
                        .collect(Collectors.toSet());
                announce.setBadges(badges);
            }

            if (announceDTO.getMapPoints() != null) {
                announce.getMapPointList().clear();
                announceDTO.getMapPoints().forEach(mpDTO -> {
                    MapPoint mapPoint = new MapPoint();
                    mapPoint.setLat(mpDTO.getLat());
                    mapPoint.setLng(mpDTO.getLng());
                    mapPoint.setAnnounce(announce);
                    announce.getMapPointList().add(mapPoint);
                });
            }

            AnnounceType type = announceTypeRepository.findById(announceDTO.getAnnounceType()).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Type not found by id: " + announceDTO.getAnnounceType()));
            announce.setAnnounceType(type);

            SaleType saleType = saleTypeRepository.findById(announceDTO.getSaleType()).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Sale type not found by id: " + announceDTO.getSaleType()));
            announce.setSaleType(saleType);

            if (announceDTO.getNearbyPlaces() != null && !announceDTO.getNearbyPlaces().isEmpty()) {
                Set<NearbyPlace> nearbyPlaces = announceDTO.getNearbyPlaces().stream()
                        .map(id -> nearbyPlaceRepository.findById(id)
                                .orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST, "NearbyPlace not found id: " + id)))
                        .collect(Collectors.toSet());
                announce.setNearbyPlaces(nearbyPlaces);
            }

            announceRepository.save(announce);
            return mapToAnnounceDTO(announce);

        }catch (WebException e){
            throw e;
        }catch (Exception e){
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Updated \"Announce\" fails " + e.getMessage());
        }
    };
    
    @Override
    public ShowAnnounceWithCategoryResponse showAnnounceWithCategory() throws WebException {
        try{
            ShowAnnounceWithCategoryResponse response = new ShowAnnounceWithCategoryResponse();
            List<Announce> announces = announceRepository.findAll();
            List<RecommendAnnounceDTO> recommendList = announces.stream()
                    .filter(announce -> announce.getBadges() != null && announce.getBadges().stream()
                            .anyMatch(badge -> badge != null && badge.getId() != null && badge.getId().equals(1L)))
                    .map(announce -> {
                        RecommendAnnounceDTO recommendAnnounce = new RecommendAnnounceDTO();
                        recommendAnnounce.setId(announce.getId());
                        recommendAnnounce.setTitle(announce.getTitle());
                        
                        if (announce.getImageList() != null && !announce.getImageList().isEmpty()) {
                            AnnounceImage firstImage = announce.getImageList().get(0);
                            if (firstImage != null && firstImage.getImageName() != null && !firstImage.getImageName().isEmpty()) {
                                recommendAnnounce.setImage(firstImage.getImageName());
                            }
                        }
                        recommendAnnounce.setBathroomCount(announce.getBathroomCount());
                        recommendAnnounce.setBedroomCount(announce.getBedroomCount());
                        recommendAnnounce.setBadges(announce.getBadges());
                        return recommendAnnounce;
                    }).limit(4)
                    .toList();
            
            List<NearbyPlaceAnnounceDTO> nearbyPlaceAnnounce = nearbyPlaceRepository.findAll().stream().filter(
                    nearbyPlace -> nearbyPlace.getType().equals(NearbyPlaceTypes.BTS_STATION)
            ).map(near -> {
                NearbyPlaceAnnounceDTO nearPlate = new NearbyPlaceAnnounceDTO();
                nearPlate.setId(near.getId());
                nearPlate.setName(near.getName());
                Integer announceCount = announces.stream()
                        .filter(announce -> announce.getNearbyPlaces().stream()
                                .anyMatch(nearplace -> nearplace.getId().equals(near.getId())))
                        .toList().size();
                nearPlate.setTotalAnnounces(announceCount);
                return nearPlate;
            }).toList();

            List<AnnounceByTypeDTO> announceByTypeList = announces.stream()
                    .filter(announce -> announce.getAnnounceType() != null && announce.getAnnounceType().getId().equals(3L))
                    .map(announce -> modelMapper.map(announce, AnnounceByTypeDTO.class)).limit(4)
                    .toList();

            List<NearbyPlaceAnnounceDTO> villaProvince = nearbyPlaceRepository.findAll().stream().filter(
                    nearbyPlace -> nearbyPlace.getType().equals(NearbyPlaceTypes.PROVINCE)
            ).map(near -> {
                NearbyPlaceAnnounceDTO nearPlate = new NearbyPlaceAnnounceDTO();
                nearPlate.setId(near.getId());
                nearPlate.setName(near.getName());
                Integer announceCount = announces.stream()
                        .filter(announce -> announce.getNearbyPlaces().stream()
                                .anyMatch(nearplace -> nearplace.getId().equals(near.getId())))
                        .toList().size();
                nearPlate.setTotalAnnounces(announceCount);
                return nearPlate;
            }).toList();
            
            response.setRecommendAnnounces(recommendList);
            response.setNearbyPlaces(nearbyPlaceAnnounce);
            response.setLuxuryHouses(announceByTypeList);
            response.setVillaProvince(villaProvince);
            
            return response;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    };
    
    @Override
    public String deletedAnnounce(Long announceId) {
        Announce announce = announceRepository.findById(announceId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce not found by id: " + announceId));
        if(!announce.getImageList().isEmpty()){
            announce.getImageList().forEach(img -> {
                announceImageService.deleteImagesByAnnounce(img.getId());
            });
        }
        
        announceRepository.delete(announce);
        return "deleted Announce successfully by Id: " + announceId;
    }
    
    @Override
    public List<AnnounceDTO> filterAnnounceWithAgen (String keyword, Integer page, Integer size) {
        return null;
    }
    
    private Announce mapToAnnounce(AnnounceDTO announceDTO){
        return modelMapper.map(announceDTO, Announce.class);
    }

    private AnnounceDTO mapToAnnounceDTO(Announce announce) {
        AnnounceDTO dto = new AnnounceDTO();
        dto.setId(announce.getId());
        dto.setTitle(announce.getTitle());
        dto.setLocation(announce.getLocation());
        dto.setPrice(announce.getPrice());
        dto.setBathroomCount(announce.getBathroomCount());
        dto.setBedroomCount(announce.getBedroomCount());
        dto.setAreaSize(announce.getAreaSize());
        dto.setHasPool(announce.getHasPool());
        dto.setHasConvenienceStore(announce.getHasConvenienceStore());
        dto.setHasFitness(announce.getHasFitness());
        dto.setHasElevator(announce.getHasElevator());
        dto.setHasParking(announce.getHasParking());
        dto.setHasSecurity(announce.getHasSecurity());
        dto.setUserId(announce.getUser().getId());
        dto.setApproveStatusId(announce.getApprove().getId());

        if (announce.getBadges() != null && !announce.getBadges().isEmpty()) {
            Set<Long> badgeIds = announce.getBadges().stream()
                    .map(Badge::getId)
                    .collect(Collectors.toSet());
            dto.setBadges(badgeIds);
        }

        if (announce.getMapPointList() != null) {
            List<MapPointDTO> mapPoints = announce.getMapPointList().stream()
                    .map(mp -> new MapPointDTO(mp.getLat(), mp.getLng()))
                    .collect(Collectors.toList());
            dto.setMapPoints(mapPoints);
        }
        
        dto.setAnnounceType(announce.getAnnounceType().getId());
        dto.setSaleType(announce.getSaleType().getId());
        
        if(announce.getNearbyPlaces() != null && !announce.getNearbyPlaces().isEmpty()) {
            Set<Long> nearbyPlaces = announce.getNearbyPlaces().stream()
                    .map(NearbyPlace::getId)
                    .collect(Collectors.toSet());
            dto.setNearbyPlaces(nearbyPlaces);
        }

        return dto;
    }
    
    private AnnounceDetailsSelected mapToAnnounceDetailsSelected (Announce announce) {
        return modelMapper.map(announce, AnnounceDetailsSelected.class);
    }
    
}
