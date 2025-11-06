package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.constants.AnnounceTypeConstant;
import com.cs.jeyz9.condoswiftapi.constants.BadgeConstant;
import com.cs.jeyz9.condoswiftapi.dto.AgentDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceApproveDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceByTypeDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDetailsSelected;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceImageDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceNearDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceRequestDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceResponse;
import com.cs.jeyz9.condoswiftapi.dto.MapPointDTO;
import com.cs.jeyz9.condoswiftapi.dto.RecommendAnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.ShowAllAnnounceDetailsWithAgent;
import com.cs.jeyz9.condoswiftapi.dto.ShowAnnounceWithCategoryResponse;
import com.cs.jeyz9.condoswiftapi.dto.TableResponse;
import com.cs.jeyz9.condoswiftapi.dto.VillaDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Announce;
import com.cs.jeyz9.condoswiftapi.models.AnnounceBadge;
import com.cs.jeyz9.condoswiftapi.models.AnnounceImage;
import com.cs.jeyz9.condoswiftapi.models.AnnounceStateApprove;
import com.cs.jeyz9.condoswiftapi.models.AnnounceType;
import com.cs.jeyz9.condoswiftapi.models.ApproveStatus;
import com.cs.jeyz9.condoswiftapi.models.Badge;
import com.cs.jeyz9.condoswiftapi.models.MapPoint;
import com.cs.jeyz9.condoswiftapi.models.SaleType;
import com.cs.jeyz9.condoswiftapi.models.Station;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.models.Villa;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceBadgeRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceImageRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceStateApproveRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.BadgeRepository;
import com.cs.jeyz9.condoswiftapi.repository.SaleTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.StationRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.repository.VillaRepository;
import com.cs.jeyz9.condoswiftapi.services.AnnounceImageService;
import com.cs.jeyz9.condoswiftapi.services.AnnounceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AnnounceServiceImpl implements AnnounceService {
    private final AnnounceRepository announceRepository;
    private final UserRepository userRepository;
    private final AnnounceStateApproveRepository announceStateApproveRepository;
    private final AnnounceImageRepository announceImageRepository;
    private final ModelMapper modelMapper;
    private final AnnounceImageService announceImageService;
    private final AnnounceTypeRepository announceTypeRepository;
    private final SaleTypeRepository saleTypeRepository;
    private final AnnounceBadgeRepository announceBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final StationRepository stationRepository;
    private final VillaRepository villaRepository;

    @Autowired
    public AnnounceServiceImpl(AnnounceRepository announceRepository, UserRepository userRepository, AnnounceStateApproveRepository announceStateApproveRepository, AnnounceImageRepository announceImageRepository, ModelMapper modelMapper, AnnounceImageService announceImageService, AnnounceTypeRepository announceTypeRepository, SaleTypeRepository saleTypeRepository, AnnounceBadgeRepository announceBadgeRepository, BadgeRepository badgeRepository, StationRepository stationRepository, VillaRepository villaRepository) {
        this.announceRepository = announceRepository;
        this.userRepository = userRepository;
        this.announceStateApproveRepository = announceStateApproveRepository;
        this.announceImageRepository = announceImageRepository;
        this.modelMapper = modelMapper;
        this.announceImageService = announceImageService;
        this.announceTypeRepository = announceTypeRepository;
        this.saleTypeRepository = saleTypeRepository;
        this.announceBadgeRepository = announceBadgeRepository;
        this.badgeRepository = badgeRepository;
        this.stationRepository = stationRepository;
        this.villaRepository = villaRepository;
    }

    @Override
    public AnnounceDetailsSelected getAnnounceDetailsById(Long announceId) {
        Announce announce = announceRepository.findById(announceId)
                .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND,
                        "Announce not found with id: " + announceId));
        List<AnnounceImage> images = announceImageRepository.findByAnnounceId(announceId);
        List<AnnounceImageDTO> imageDTOS = images.stream().map(img -> modelMapper.map(img, AnnounceImageDTO.class)).toList();
        
        User user = userRepository.findById(announce.getUser().getId()).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not fond by id" + announce.getUser().getId()));
        AgentDTO agen = modelMapper.map(user, AgentDTO.class);
        agen.setIsVerify(user.getEmailVerified().equals(true) && user.getPhoneVerified().equals(true));
        
        AnnounceDetailsSelected announceDetailsSelected = mapToAnnounceDetailsSelected(announce);
        announceDetailsSelected.setImageList(imageDTOS);
        announceDetailsSelected.setAgent(agen);
        announceDetailsSelected.setMapPoint(
                announce.getMapPointList()
                        .stream()
                        .findFirst()
                        .map(map -> modelMapper.map(map, MapPointDTO.class))
                        .orElse(new MapPointDTO())
        );
        return announceDetailsSelected;
    }
    
    @Override
    public Announce getAnnounceById(Long announceId) {
        return announceRepository.findById(announceId)
                .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND,
                        "Announce not found with id: " + announceId));
    }

    @Override
    public AnnounceRequestDTO addAnnounceWithImage(AnnounceDTO announceDTO, List<MultipartFile> imageFile) throws WebException {
        try{
            Announce announce = new Announce();
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

            if (announceDTO.getMapPoints() != null) {
                announceDTO.getMapPoints().forEach(mpDTO -> {
                    MapPoint mapPoint = new MapPoint();
                    mapPoint.setLat(mpDTO.getLat());
                    mapPoint.setLng(mpDTO.getLng());
                    mapPoint.setCreatedAt(LocalDateTime.now());
                    mapPoint.setAnnounce(announce);
                    announce.getMapPointList().add(mapPoint);
                });
            }

            AnnounceType type = announceTypeRepository.findById(announceDTO.getAnnounceType()).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Type not found by id: " + announceDTO.getAnnounceType()));
            announce.setAnnounceType(type);

            SaleType saleType = saleTypeRepository.findById(announceDTO.getSaleType()).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Sale type not found by id: " + announceDTO.getSaleType()));
            announce.setSaleType(saleType);

            Announce response = announceRepository.save(announce);

            Badge badge = badgeRepository.findByBadgeNameIgnoreCase(BadgeConstant.NEW).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Badge not found."));
            AnnounceBadge announceBadge = new AnnounceBadge();
            announceBadge.setBadge(badge);
            announceBadge.setAnnounce(response);
            announceBadge.setExpiresAt(LocalDateTime.now().plusDays(7));
            announceBadgeRepository.save(announceBadge);
            
            announceImageService.saveImages(response.getId(), imageFile);
            
            return mapToAnnounceRequestDTO(announce);

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
                    .filter(announce -> announce.getAnnounceBadges() != null && announce.getAnnounceBadges().stream()
                            .anyMatch(badge -> badge != null && badge.getId() != null && badge.getBadge().getBadgeName().equalsIgnoreCase(BadgeConstant.RECOMMEND)))
                    .map(announce -> {
                        RecommendAnnounceDTO recommendAnnounce = new RecommendAnnounceDTO();
                        recommendAnnounce.setId(announce.getId());
                        recommendAnnounce.setTitle(announce.getTitle());
                        recommendAnnounce.setPrice(announce.getPrice());
                        
                        if (announce.getImageList() != null && !announce.getImageList().isEmpty()) {
                            AnnounceImage firstImage = announce.getImageList().get(0);
                            if (firstImage != null && firstImage.getImageUrl() != null && !firstImage.getImageUrl().isEmpty()) {
                                recommendAnnounce.setImage(firstImage.getImageUrl());
                            }
                        }
                        recommendAnnounce.setBathroomCount(announce.getBathroomCount());
                        recommendAnnounce.setBedroomCount(announce.getBedroomCount());
                        recommendAnnounce.setAreaSize(announce.getAreaSize());
                        recommendAnnounce.setBadges(
                                announce.getAnnounceBadges().stream()
                                        .map(AnnounceBadge::getBadge)
                                        .toList()
                        );
                        return recommendAnnounce;
                    }).limit(4)
                    .toList();
            
            List<Station> stationList = stationRepository.findAll().stream().toList();
            List<AnnounceNearDTO> announceNearDTO = stationList.stream().map(station -> {
                AnnounceNearDTO announceNearStation = new AnnounceNearDTO();
                announceNearStation.setId(station.getId());
                announceNearStation.setName(station.getName());
                Long countAnnounceNear = announceRepository.countListingsNear(station.getLat(), station.getLng(), 1.5);
                announceNearStation.setTotalAnnounce(countAnnounceNear);
                return announceNearStation;
            }).sorted(Comparator.comparingLong(AnnounceNearDTO::getTotalAnnounce).reversed()).limit(4).toList();

            List<AnnounceByTypeDTO> announceByTypeList = announces.stream()
                    .filter(announce -> announce.getAnnounceType() != null && announce.getAnnounceType().getTypeName().equals(AnnounceTypeConstant.LUXURY_HOUSE))
                    .map(announce -> {
                        AnnounceByTypeDTO announceByType = new AnnounceByTypeDTO();
                        announceByType.setId(announce.getId());
                        announceByType.setTitle(announce.getTitle());
                        announceByType.setLocation(announce.getLocation());
                        String imageUrl = Optional.ofNullable(announce.getImageList())
                                .flatMap(list -> list.stream().findFirst())
                                .map(AnnounceImage::getImageUrl)
                                .orElse(null);
                        announceByType.setImage(imageUrl);
                        return announceByType;
                    }).limit(4)
                    .toList();
            
            List<Villa> villaList = villaRepository.findAll();
            List<VillaDTO> villaNear = villaList.stream().map(villa -> {
                VillaDTO villaNearProvince = new VillaDTO();
                villaNearProvince.setId(villa.getId());
                villaNearProvince.setName(villa.getName());
                villaNearProvince.setTotalAnnounce(announceRepository.countVillaInProvince(villa.getProvince()));
                villaNearProvince.setProvince(villa.getProvince());
                return villaNearProvince;
            }).sorted(Comparator.comparingLong(VillaDTO::getTotalAnnounce).reversed()).limit(4).toList();
            
            response.setRecommendAnnounces(recommendList);
            response.setNearbyPlaces(announceNearDTO);
            response.setLuxuryHouses(announceByTypeList);
            response.setVillaProvince(villaNear);
            
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
    public AnnounceResponse filterAnnounceWithAgen (String keyword, String type, String station, String province, String saleType, Integer bedroomCount, String badge, Double minPrice, Double maxPrice, Integer page, Integer size) throws IOException {
        try{
            List<Announce> announceList;
            if(station != null && !station.trim().isEmpty()){
                announceList = announceRepository.findAnnounceNearStation(station);
            }else {
                announceList = findAllAnnounce();
            }
            
            Stream<Announce> stream = announceList.stream();
            if (keyword != null && !keyword.trim().isEmpty()) {
                stream = stream.filter(a ->
                        a.getTitle().toLowerCase().contains(keyword.toLowerCase())
                );
            }
    
            if (type != null && !type.trim().isEmpty()) {
                stream = stream.filter(a -> a.getAnnounceType().getTypeName().equalsIgnoreCase(type));
            }
            
            if(province != null && !province.trim().isEmpty()){
                stream = stream.filter(a -> a.getLocation().toLowerCase().contains(province.toLowerCase()));
            }

            if (saleType != null && !saleType.trim().isEmpty()) {
                stream = stream.filter(a -> a.getSaleType().getType().equalsIgnoreCase(saleType));
            }
    
            if (bedroomCount != null) {
                stream = stream.filter(a -> a.getBedroomCount() != null && a.getBedroomCount().equals(bedroomCount));
            }
            
            if(badge != null && !badge.trim().isEmpty()) {
                stream = stream.filter(a -> a.getAnnounceBadges() != null && a.getAnnounceBadges().stream().anyMatch(b -> b.getBadge().getBadgeName().equalsIgnoreCase(badge)));
            }
    
            if (minPrice != null) {
                stream = stream.filter(a -> a.getPrice() != null && a.getPrice() >= minPrice);
            }
    
            if (maxPrice != null) {
                stream = stream.filter(a -> a.getPrice() != null && a.getPrice() <= maxPrice);
            }
            
            List<ShowAllAnnounceDetailsWithAgent> announces = mapToShowAllAnnounce(stream.toList());
    
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), announces.size());
            int total = announces.size();
            
            List<ShowAllAnnounceDetailsWithAgent> paginatedList = announces.subList(start, end);
            Page<ShowAllAnnounceDetailsWithAgent> announcePage = new PageImpl<>(paginatedList, pageable, announces.size());
            
            AnnounceResponse announceResponse = new AnnounceResponse();
            announceResponse.setAnnounceDetailsWithAgents(announcePage.getContent());
            announceResponse.setPage(page);
            announceResponse.setSize(size);
            announceResponse.setTotal(total);
            return announceResponse;
        }catch(Exception err) {
            throw new IOException("Error while searching for announce", err);
        }
    }

    @Override
    public TableResponse<AnnounceApproveDTO> showAllAnnouncePending(String keyword, Integer page, Integer size) throws IOException {
        try{
            List<AnnounceApproveDTO> announce = announceRepository.findAnnouncePending();
            Stream<AnnounceApproveDTO> stream = announce.stream();
            if(keyword != null && !keyword.trim().isEmpty()){
                stream = stream.filter(ann -> ann.getTitle().toLowerCase().contains(keyword.toLowerCase()) || ann.getAgenName().toLowerCase().contains(keyword.toLowerCase()));
            }

            List<AnnounceApproveDTO> announceApproveList = stream.toList();
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), announceApproveList.size());
            int total = announceApproveList.size();

            List<AnnounceApproveDTO> paginatedList = announceApproveList.subList(start, end);
            Page<AnnounceApproveDTO> announceApprovePage = new PageImpl<>(paginatedList, pageable, announceApproveList.size());

            TableResponse<AnnounceApproveDTO> announceApproveTableResponse = new TableResponse<>();
            announceApproveTableResponse.setData(announceApprovePage.getContent());
            announceApproveTableResponse.setPage(page);
            announceApproveTableResponse.setSize(size);
            announceApproveTableResponse.setTotal(total);
            return announceApproveTableResponse;

        }catch (Exception e) {
            throw new IOException("Error while searching for badge", e);
        }
    }
    
    @Override
    public TableResponse<AnnounceApproveDTO> showAllAnnounceApprove(String keyword, Integer page, Integer size) throws IOException {
        try{
            List<AnnounceApproveDTO> announce = announceRepository.findAnnounceApprove();
            Stream<AnnounceApproveDTO> stream = announce.stream();
            if(keyword != null && !keyword.trim().isEmpty()){
                stream = stream.filter(ann -> ann.getTitle().toLowerCase().contains(keyword.toLowerCase()) || ann.getAgenName().toLowerCase().contains(keyword.toLowerCase()));
            }
            
            List<AnnounceApproveDTO> announceApproveList = stream.toList();
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), announceApproveList.size());
            int total = announceApproveList.size();
            
            List<AnnounceApproveDTO> paginatedList = announceApproveList.subList(start, end);
            Page<AnnounceApproveDTO> announceApprovePage = new PageImpl<>(paginatedList, pageable, announceApproveList.size());
            
            TableResponse<AnnounceApproveDTO> announceApproveTableResponse = new TableResponse<>();
            announceApproveTableResponse.setData(announceApprovePage.getContent());
            announceApproveTableResponse.setPage(page);
            announceApproveTableResponse.setSize(size);
            announceApproveTableResponse.setTotal(total);
            return announceApproveTableResponse;
            
        }catch (Exception e) {
            throw new IOException("Error while searching for badge", e);
        }
    }

    @Override
    public TableResponse<AnnounceApproveDTO> showAllAnnounceHistory(String keyword, Integer page, Integer size) throws IOException {
        try{
            List<AnnounceApproveDTO> announce = announceRepository.findAnnounceHistory();
            Stream<AnnounceApproveDTO> stream = announce.stream();
            if(keyword != null && !keyword.trim().isEmpty()){
                stream = stream.filter(ann -> ann.getTitle().toLowerCase().contains(keyword.toLowerCase()) || ann.getAgenName().toLowerCase().contains(keyword.toLowerCase()));
            }

            List<AnnounceApproveDTO> announceApproveList = stream.toList();
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), announceApproveList.size());
            int total = announceApproveList.size();

            List<AnnounceApproveDTO> paginatedList = announceApproveList.subList(start, end);
            Page<AnnounceApproveDTO> announceApprovePage = new PageImpl<>(paginatedList, pageable, announceApproveList.size());

            TableResponse<AnnounceApproveDTO> announceApproveTableResponse = new TableResponse<>();
            announceApproveTableResponse.setData(announceApprovePage.getContent());
            announceApproveTableResponse.setPage(page);
            announceApproveTableResponse.setSize(size);
            announceApproveTableResponse.setTotal(total);
            return announceApproveTableResponse;

        }catch (Exception e) {
            throw new IOException("Error while searching for badge", e);
        }
    }
    
    private List<Announce> findAllAnnounce() {
        return announceRepository.findAll();
    }

    private List<AnnounceApproveDTO> mapToAnnounceApprove(List<Announce> announces) {
        return announces.stream().map(ann -> modelMapper.map(announces, AnnounceApproveDTO.class)).toList();
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

        if (announce.getMapPointList() != null) {
            List<MapPointDTO> mapPoints = announce.getMapPointList().stream()
                    .map(mp -> new MapPointDTO(mp.getLat(), mp.getLng()))
                    .collect(Collectors.toList());
            dto.setMapPoints(mapPoints);
        }
        
        dto.setAnnounceType(announce.getAnnounceType().getId());
        dto.setSaleType(announce.getSaleType().getId());
        
        return dto;
    }

    private AnnounceRequestDTO mapToAnnounceRequestDTO(Announce announce) {
        AnnounceRequestDTO dto = new AnnounceRequestDTO();
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

        if (announce.getMapPointList() != null) {
            List<MapPointDTO> mapPoints = announce.getMapPointList().stream()
                    .map(mp -> new MapPointDTO(mp.getLat(), mp.getLng()))
                    .collect(Collectors.toList());
            dto.setMapPoints(mapPoints);
        }

        dto.setAnnounceType(announce.getAnnounceType().getId());
        dto.setSaleType(announce.getSaleType().getId());

        return dto;
    }
    
    private AnnounceDetailsSelected mapToAnnounceDetailsSelected (Announce announce) {
        return modelMapper.map(announce, AnnounceDetailsSelected.class);
    }
    
    private List<ShowAllAnnounceDetailsWithAgent> mapToShowAllAnnounce(List<Announce> announce) {
        return announce.stream().map(ann -> {
            ShowAllAnnounceDetailsWithAgent showAllAnnounceDetailsWithAgen = new ShowAllAnnounceDetailsWithAgent();
            showAllAnnounceDetailsWithAgen.setId(ann.getId());
            showAllAnnounceDetailsWithAgen.setTitle(ann.getTitle());
            showAllAnnounceDetailsWithAgen.setPrice(ann.getPrice());
            showAllAnnounceDetailsWithAgen.setImageList(
                    ann.getImageList().stream().findFirst().map(img -> modelMapper.map(img, AnnounceImageDTO.class)).orElse(new AnnounceImageDTO())
            );
            showAllAnnounceDetailsWithAgen.setAgent(modelMapper.map(ann.getUser(), AgentDTO.class));
            return modelMapper.map(showAllAnnounceDetailsWithAgen, ShowAllAnnounceDetailsWithAgent.class);
        }).toList();
    }
}
