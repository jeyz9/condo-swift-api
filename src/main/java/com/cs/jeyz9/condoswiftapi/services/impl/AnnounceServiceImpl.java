package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.constants.AnnounceTypeConstant;
import com.cs.jeyz9.condoswiftapi.constants.BadgeConstant;
import com.cs.jeyz9.condoswiftapi.dto.AgentDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceApproveDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceByTypeDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDetailsSelected;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDraftDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceImageDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceNearDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceRequestDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceResponse;
import com.cs.jeyz9.condoswiftapi.dto.BadgeDTO;
import com.cs.jeyz9.condoswiftapi.dto.BadgeDetailsDTO;
import com.cs.jeyz9.condoswiftapi.dto.MapPointDTO;
import com.cs.jeyz9.condoswiftapi.dto.RecommendAnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.RejectAnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.ShowAllAnnounceBadgesDTO;
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
import com.cs.jeyz9.condoswiftapi.models.Province;
import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.models.RoleName;
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
import com.cs.jeyz9.condoswiftapi.repository.ProvinceRepository;
import com.cs.jeyz9.condoswiftapi.repository.SaleTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.StationRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.repository.VillaRepository;
import com.cs.jeyz9.condoswiftapi.services.AnnounceImageService;
import com.cs.jeyz9.condoswiftapi.services.AnnounceService;
import com.cs.jeyz9.condoswiftapi.services.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private final ProvinceRepository provinceRepository;
    private final NotificationService notificationService;

    @Autowired
    public AnnounceServiceImpl(AnnounceRepository announceRepository, 
                               UserRepository userRepository, 
                               AnnounceStateApproveRepository announceStateApproveRepository, 
                               AnnounceImageRepository announceImageRepository, 
                               ModelMapper modelMapper, 
                               AnnounceImageService announceImageService, 
                               AnnounceTypeRepository announceTypeRepository, 
                               SaleTypeRepository saleTypeRepository, 
                               AnnounceBadgeRepository announceBadgeRepository, 
                               BadgeRepository badgeRepository, 
                               StationRepository stationRepository, 
                               VillaRepository villaRepository, 
                               ProvinceRepository provinceRepository, 
                               NotificationService notificationService) {
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
        this.provinceRepository = provinceRepository;
        this.notificationService = notificationService;
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
    @Transactional
    public AnnounceDTO addAnnounceWithImage(AnnounceRequestDTO announceDTO, List<MultipartFile> imageFile) throws WebException {
        try{
            Announce announce = new Announce();
            Province province = provinceRepository.findByName(announceDTO.getProvince()).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Province not found."));
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
            announce.setProvince(province);
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
            
            return mapToAnnounceDTO(response);

        }catch (WebException e){
            throw e;
        }catch (Exception e){
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Added \"Announce\" fails " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public AnnounceDTO updateAnnounceWithImage(Long announceId, AnnounceRequestDTO announceDTO, List<MultipartFile> imageFiles) throws WebException {
        try {

            Announce announce = announceRepository.findById(announceId)
                    .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce not found by id: " + announceId));

            Province province = provinceRepository.findByName(announceDTO.getProvince())
                    .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Province not found."));

            User user = userRepository.findById(announceDTO.getUserId())
                    .orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST,
                            "User not found by id: " + announceDTO.getUserId()));

            AnnounceStateApprove approveStatus = announceStateApproveRepository.findById(announceDTO.getApproveStatusId())
                    .orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST,
                            "Approve Status not found by id: " + announceDTO.getApproveStatusId()));

            if (!approveStatus.getStatusName().equals(ApproveStatus.DRAFT)
                    && !approveStatus.getStatusName().equals(ApproveStatus.PENDING)) {
                throw new WebException(HttpStatus.BAD_REQUEST, "Approve status must be DRAFT or PENDING only");
            }

            AnnounceType type = announceTypeRepository.findById(announceDTO.getAnnounceType())
                    .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND,
                            "Type not found by id: " + announceDTO.getAnnounceType()));

            SaleType saleType = saleTypeRepository.findById(announceDTO.getSaleType())
                    .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND,
                            "Sale type not found by id: " + announceDTO.getSaleType()));

            BeanUtils.copyProperties(announceDTO, announce, "id", "mapPoints");
            announce.setProvince(province);
            announce.setUser(user);
            announce.setApprove(approveStatus);
            announce.setAnnounceType(type);
            announce.setSaleType(saleType);
            announce.getMapPointList().clear();

            if (announceDTO.getMapPoints() != null && !announceDTO.getMapPoints().isEmpty()) {
                announceDTO.getMapPoints().forEach(mpDTO -> {
                    MapPoint mp = new MapPoint();
                    mp.setLat(mpDTO.getLat());
                    mp.setLng(mpDTO.getLng());
                    mp.setCreatedAt(LocalDateTime.now());
                    mp.setAnnounce(announce);
                    announce.getMapPointList().add(mp);
                });
            }
            Announce saved = announceRepository.save(announce);
            if (imageFiles != null && !imageFiles.isEmpty()) {
                announceImageService.saveImages(saved.getId(), imageFiles);
            }


            return mapToAnnounceDTO(saved);

        } catch (WebException e) {
            throw e;
        } catch (Exception e) {
            throw new WebException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Update \"Announce\" fails: " + e.getMessage()
            );
        }
    }

    @Override
    public ShowAnnounceWithCategoryResponse showAnnounceWithCategory() throws WebException {
        try {

            ShowAnnounceWithCategoryResponse response = new ShowAnnounceWithCategoryResponse();

            List<Announce> announces = announceRepository.findAll();

            Map<String, Integer> priorityMap = Map.of(
                    BadgeConstant.PREMIUM, 1,
                    BadgeConstant.RECOMMEND, 2,
                    BadgeConstant.NEW, 3
            );
            
            List<RecommendAnnounceDTO> recommendList = announces.stream()
                    .filter(at -> at.getAnnounceType() != null && AnnounceTypeConstant.CONDO.equalsIgnoreCase(at.getAnnounceType().getTypeName()))
                    .sorted((a, b) -> compareBadgePriority(a, b, priorityMap))
                    .limit(4)
                    .map(this::mapToRecommendDTO)
                    .toList();
            
            List<AnnounceNearDTO> announceNearDTO = stationRepository.findAll().stream()
                    .map(station -> {
                        AnnounceNearDTO dto = new AnnounceNearDTO();
                        dto.setId(station.getId());
                        dto.setName(station.getName());
                        dto.setTotalAnnounce(
                                announceRepository.countListingsNear(station.getLat(), station.getLng(), 1.5)
                        );
                        return dto;
                    })
                    .sorted(Comparator.comparingLong(AnnounceNearDTO::getTotalAnnounce).reversed())
                    .limit(4)
                    .toList();
            
            List<AnnounceByTypeDTO> announceByTypeList = announces.stream()
                    .filter(a -> a.getAnnounceType() != null &&
                            AnnounceTypeConstant.LUXURY_HOUSE.equalsIgnoreCase(a.getAnnounceType().getTypeName()))
                    .sorted((a, b) -> compareBadgePriority(a, b, priorityMap))
                    .map(this::mapToLuxuryHouseDTO)
                    .limit(4)
                    .toList();
            
            List<VillaDTO> villaNear = villaRepository.findAll().stream()
                    .map(villa -> {
                        VillaDTO dto = new VillaDTO();
                        dto.setId(villa.getId());
                        dto.setName(villa.getName());
                        dto.setProvince(villa.getProvince());
                        dto.setTotalAnnounce(announceRepository.countVillaInProvince(villa.getProvince()));
                        return dto;
                    })
                    .sorted(Comparator.comparingLong(VillaDTO::getTotalAnnounce).reversed())
                    .limit(4)
                    .toList();
            
            response.setRecommendAnnounces(recommendList);
            response.setNearbyPlaces(announceNearDTO);
            response.setLuxuryHouses(announceByTypeList);
            response.setVillaProvince(villaNear);

            return response;

        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    
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
                stream = stream.filter(a -> a.getProvince().getName().equalsIgnoreCase(province));
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
                stream = stream.filter(ann -> ann.getTitle().toLowerCase().contains(keyword.toLowerCase()) || ann.getAgentName().toLowerCase().contains(keyword.toLowerCase()));
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
                stream = stream.filter(ann -> ann.getTitle().toLowerCase().contains(keyword.toLowerCase()) || ann.getAgentName().toLowerCase().contains(keyword.toLowerCase()));
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
                stream = stream.filter(ann -> ann.getTitle().toLowerCase().contains(keyword.toLowerCase()) || ann.getAgentName().toLowerCase().contains(keyword.toLowerCase()));
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
    @Transactional
    public String approveAnnounce(Long announceId, String officialEmail) {
        try {
            Announce announce = announceRepository.findById(announceId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce not found."));
            
            if(announce.getApprove().getStatusName().equals(ApproveStatus.APPROVED)){
                throw new WebException(HttpStatus.BAD_REQUEST, "This announcement has been approved.");
            }
            
            User official = userRepository.findByEmail(officialEmail).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Official not found."));
            AnnounceStateApprove status = announceStateApproveRepository.findByStatusName(ApproveStatus.APPROVED);

            RoleName role = official.getRoles().stream()
                    .findFirst()
                    .map(Role::getRoleName).orElse(null);
            
            announce.setApprove(status);
            announce.setApproveBy(official);
            announce.setRemark("อนุมัติโดย " + role);
            announce.setApproveDate(LocalDateTime.now());
            announceRepository.save(announce);
            notificationService.systemSendNotification(announce.getUser(), "ประกาศของคุณได้รับการอนุมัติแล้ว", "เจ้าหน้าที่ได้ทำการตรวจสอบและอนุมัติประกาศ " + announce.getTitle() + " ของคุณเรียบร้อยแล้ว");
            
            return "Approve announce success.";
        } catch (WebException e) {
            throw e;
        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public String rejectAnnounce(Long announceId, String officialEmail, RejectAnnounceDTO reject) {
        try {
            Announce announce = announceRepository.findById(announceId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce not found."));

            if(announce.getApprove().getStatusName().equals(ApproveStatus.REJECTED)){
                throw new WebException(HttpStatus.BAD_REQUEST, "This announcement has been rejected.");
            }

            User official = userRepository.findByEmail(officialEmail).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Official not found."));
            AnnounceStateApprove status = announceStateApproveRepository.findByStatusName(ApproveStatus.REJECTED);

            announce.setApprove(status);
            announce.setApproveBy(official);
            announce.setRemark(reject.getRemark());
            announce.setApproveDate(LocalDateTime.now());
            announceRepository.save(announce);
            notificationService.systemSendNotification(announce.getUser(), "ประกาศของคุณถูกปฏิเสธ", "ประกาศ " + announce.getTitle() + " ของคุณถูกปฏิเสธเนื่องจาก " + reject.getRemark());
            
            return "Reject announce success.";
        } catch (WebException e) {
            throw e;
        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public TableResponse<ShowAllAnnounceBadgesDTO> showAllAnnounceBadgesSelector(String keyword, String badges, Integer page, Integer size) throws IOException {
        try {
            List<ShowAllAnnounceBadgesDTO> announceBadges = showAllAnnounceBadges();
            Stream<ShowAllAnnounceBadgesDTO> stream = announceBadges.stream();
            if (keyword != null && !keyword.trim().isEmpty()) {
                stream = stream.filter(a -> a.getTitle().toLowerCase().contains(keyword.toLowerCase()));
            }

            if (badges != null && !badges.trim().isEmpty()) {
                stream = stream.filter(a -> a.getBadges().stream().anyMatch(b -> b.getBadgeName().toLowerCase().contains(badges.toLowerCase())));
            }

            List<ShowAllAnnounceBadgesDTO> announceBadgesList = stream.toList();
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), announceBadgesList.size());
            int total = announceBadgesList.size();

            List<ShowAllAnnounceBadgesDTO> paginatedList = announceBadgesList.subList(start, end);
            Page<ShowAllAnnounceBadgesDTO> announcePage = new PageImpl<>(paginatedList, pageable, announceBadgesList.size());

            TableResponse<ShowAllAnnounceBadgesDTO> announceResponse = new TableResponse<>();
            announceResponse.setData(announcePage.getContent());
            announceResponse.setPage(page);
            announceResponse.setSize(size);
            announceResponse.setTotal(total);
            return announceResponse;
        }catch (Exception err) {
            throw new IOException("Error while searching for announce", err);
        }
    }

    @Override
    public List<AnnounceDraftDTO> showAllAnnounceDraft(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
        List<Announce> announces = announceRepository.findAllByUserId(user.getId());
        announces = announces.stream().filter(a -> 
            a.getApprove().getStatusName().equals(ApproveStatus.DRAFT)
        ).toList();
        return mapToAnnounceDraft(announces);
    }

    private List<ShowAllAnnounceBadgesDTO> showAllAnnounceBadges() {
        List<Announce> announces = announceRepository.findAll();
        return announces.stream().map(a -> {
            Set<BadgeDetailsDTO> badgeList = a.getAnnounceBadges().stream().map( ab ->
                    announceBadgeRepository.findAnnounceBadgesByAnnounceIdAndBadgeId(ab.getId()).orElse(null)
            ).collect(Collectors.toSet());
            return ShowAllAnnounceBadgesDTO.builder()
                    .id(a.getId())
                    .title(a.getTitle())
                    .agent(a.getUser().getName())
                    .badges(badgeList)
                    .build();
        }).toList();
    }

    private List<Announce> findAllAnnounce() {

        Map<String, Integer> priorityMap = Map.of(
                BadgeConstant.PREMIUM, 1,
                BadgeConstant.RECOMMEND, 2,
                BadgeConstant.NEW, 3
        );

        List<Announce> announces = announceRepository.findAll();

        return announces.stream()
                .sorted((a, b) -> {

                    int priorityA = Optional.ofNullable(a.getAnnounceBadges())
                            .orElse(Set.of())
                            .stream()
                            .map(ab -> priorityMap.getOrDefault(ab.getBadge().getBadgeName(), Integer.MAX_VALUE))
                            .min(Integer::compareTo)
                            .orElse(Integer.MAX_VALUE);

                    int priorityB = Optional.ofNullable(b.getAnnounceBadges())
                            .orElse(Set.of())
                            .stream()
                            .map(ab -> priorityMap.getOrDefault(ab.getBadge().getBadgeName(), Integer.MAX_VALUE))
                            .min(Integer::compareTo)
                            .orElse(Integer.MAX_VALUE);

                    return Integer.compare(priorityA, priorityB);
                })
                .toList();
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
        dto.setProvince(announce.getProvince().getName());

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
            Set<Badge> badges = badgeRepository.findAllBadgeByAnnounceId(ann.getId());
            showAllAnnounceDetailsWithAgen.setId(ann.getId());
            showAllAnnounceDetailsWithAgen.setTitle(ann.getTitle());
            showAllAnnounceDetailsWithAgen.setPrice(ann.getPrice());
            showAllAnnounceDetailsWithAgen.setImageList(
                    ann.getImageList().stream().findFirst().map(img -> modelMapper.map(img, AnnounceImageDTO.class)).orElse(new AnnounceImageDTO())
            );
            showAllAnnounceDetailsWithAgen.setAddress(ann.getLocation());
            showAllAnnounceDetailsWithAgen.setAgent(mapToAgentDTO(ann.getUser()));
            showAllAnnounceDetailsWithAgen.setBadgeSet(mapToBadgeDTO(badges));
            return modelMapper.map(showAllAnnounceDetailsWithAgen, ShowAllAnnounceDetailsWithAgent.class);
        }).toList();
    }
    
    private List<AnnounceDraftDTO> mapToAnnounceDraft(List<Announce> announces){
        return announces.stream().map(a -> {
            AnnounceDraftDTO draft = new AnnounceDraftDTO();
            draft.setId(a.getId());
            draft.setTitle(a.getTitle());
            draft.setImageList(
                    a.getImageList().stream().findFirst().map(img -> modelMapper.map(img, AnnounceImageDTO.class)).orElse(new AnnounceImageDTO())
            );
            draft.setAddress(a.getLocation());
            draft.setPrice(a.getPrice());
            
            return modelMapper.map(draft, AnnounceDraftDTO.class);
        }).toList();
    }
    
    private AgentDTO mapToAgentDTO(User agent) {
        AgentDTO agentDTO = modelMapper.map(agent, AgentDTO.class);
        agentDTO.setIsVerify(agent.getEmailVerified() && agent.getPhoneVerified());
        return agentDTO;
    }
    
    private Set<BadgeDTO> mapToBadgeDTO(Set<Badge> badge) {
        return badge.stream().map(bg -> modelMapper.map(bg, BadgeDTO.class)).collect(Collectors.toSet());
    }

    private int compareBadgePriority(Announce a, Announce b, Map<String, Integer> priorityMap) {

        int pA = Optional.ofNullable(a.getAnnounceBadges())
                .orElse(Set.of())
                .stream()
                .map(ab -> priorityMap.getOrDefault(ab.getBadge().getBadgeName().toUpperCase(), Integer.MAX_VALUE))
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);

        int pB = Optional.ofNullable(b.getAnnounceBadges())
                .orElse(Set.of())
                .stream()
                .map(ab -> priorityMap.getOrDefault(ab.getBadge().getBadgeName().toUpperCase(), Integer.MAX_VALUE))
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);

        return Integer.compare(pA, pB);
    }

    private RecommendAnnounceDTO mapToRecommendDTO(Announce announce) {
        RecommendAnnounceDTO dto = new RecommendAnnounceDTO();
        dto.setId(announce.getId());
        dto.setTitle(announce.getTitle());
        dto.setPrice(announce.getPrice());
        dto.setBathroomCount(announce.getBathroomCount());
        dto.setBedroomCount(announce.getBedroomCount());
        dto.setAreaSize(announce.getAreaSize());

        if (announce.getImageList() != null && !announce.getImageList().isEmpty()) {
            dto.setImage(announce.getImageList().get(0).getImageUrl());
        }

        dto.setBadges(
                announce.getAnnounceBadges().stream()
                        .map(AnnounceBadge::getBadge)
                        .toList()
        );

        return dto;
    }

    private AnnounceByTypeDTO mapToLuxuryHouseDTO(Announce a) {
        AnnounceByTypeDTO dto = new AnnounceByTypeDTO();
        dto.setId(a.getId());
        dto.setTitle(a.getTitle());
        dto.setLocation(a.getLocation());

        dto.setImage(
                Optional.ofNullable(a.getImageList())
                        .flatMap(list -> list.stream().findFirst())
                        .map(AnnounceImage::getImageUrl)
                        .orElse(null)
        );

        return dto;
    }
}
