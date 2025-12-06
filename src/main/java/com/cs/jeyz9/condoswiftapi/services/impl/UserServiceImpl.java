package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.constants.SaleTypeConstant;
import com.cs.jeyz9.condoswiftapi.dto.AgentDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceByTypeDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceImageDTO;
import com.cs.jeyz9.condoswiftapi.dto.BadgeDTO;
import com.cs.jeyz9.condoswiftapi.dto.EditProfileDTO;
import com.cs.jeyz9.condoswiftapi.dto.RecommendedAgenDTO;
import com.cs.jeyz9.condoswiftapi.dto.ShowAllAnnounceDetailsWithAgent;
import com.cs.jeyz9.condoswiftapi.dto.ShowAllUserDTO;
import com.cs.jeyz9.condoswiftapi.dto.ShowUserDetailsDTO;
import com.cs.jeyz9.condoswiftapi.dto.UserProfileOverviewDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Announce;
import com.cs.jeyz9.condoswiftapi.models.AnnounceImage;
import com.cs.jeyz9.condoswiftapi.models.Badge;
import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.models.RoleName;
import com.cs.jeyz9.condoswiftapi.models.Terms;
import com.cs.jeyz9.condoswiftapi.models.TermsType;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.models.UserTermsAcceptLog;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceRepository;
import com.cs.jeyz9.condoswiftapi.repository.BadgeRepository;
import com.cs.jeyz9.condoswiftapi.repository.RoleRepository;
import com.cs.jeyz9.condoswiftapi.repository.TermsRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserTermsAcceptLogRepository;
import com.cs.jeyz9.condoswiftapi.services.NotificationService;
import com.cs.jeyz9.condoswiftapi.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TermsRepository termsRepository;
    private final UserTermsAcceptLogRepository userTermsAcceptLogRepository;
    private final RoleRepository roleRepository;
    private final AnnounceRepository announceRepository;
    private final ModelMapper modelMapper;
    private final BadgeRepository badgeRepository;
    private final NotificationService notificationService;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket.profile}")
    private String bucket;

    public UserServiceImpl(UserRepository userRepository, TermsRepository termsRepository, UserTermsAcceptLogRepository userTermsAcceptLogRepository, RoleRepository roleRepository, AnnounceRepository announceRepository, ModelMapper modelMapper, BadgeRepository badgeRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.termsRepository = termsRepository;
        this.userTermsAcceptLogRepository = userTermsAcceptLogRepository;
        this.roleRepository = roleRepository;
        this.announceRepository = announceRepository;
        this.modelMapper = modelMapper;
        this.badgeRepository = badgeRepository;
        this.notificationService = notificationService;
    }

    @Override
    public String userTermsAcceptLog(Long userId, HttpServletRequest request) throws WebException {
        try{
            User user = userRepository.findById(userId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found by id: " + userId));
            Terms terms = termsRepository.findByTypeAndIsActiveTrue(TermsType.AGENT_CONTACT_POLICY).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Terms not found."));
            UserTermsAcceptLog acceptLog = new UserTermsAcceptLog();
            acceptLog.setUser(user);
            acceptLog.setTerms(terms);
            acceptLog.setIpAddress(request.getRemoteAddr());
            acceptLog.setUserAgent(request.getHeader("User-Agent"));
            userTermsAcceptLogRepository.save(acceptLog);
            return "Accept terms success";
        } catch (WebException e) {
            throw e;
        }catch (Exception e){
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Terms Accept fail " + e.getMessage());
        }
    }
    
    @Override
    public List<RecommendedAgenDTO> showRecommendedAgents() {
        Set<Role> role = roleRepository.findRoleByRoleName(RoleName.AGENT);
        List<User> user = userRepository.findUserByRoles(role);
        return user.stream()
                .filter(agent -> agent.getEmailVerified() && agent.getPhoneVerified())
                .map(agent -> {
                    RecommendedAgenDTO recommendedAgenDTO = new RecommendedAgenDTO();
                    recommendedAgenDTO.setId(agent.getId());
                    recommendedAgenDTO.setName(agent.getName());
                    recommendedAgenDTO.setDescription(agent.getDescription());
                    recommendedAgenDTO.setImage(agent.getImage());
                    recommendedAgenDTO.setPhone(agent.getPhone());
                    recommendedAgenDTO.setLineId(agent.getLineId());
                    recommendedAgenDTO.setIsVerified(agent.getEmailVerified() && agent.getPhoneVerified());    
                    return recommendedAgenDTO;         
        }).limit(3).toList();
    }

    @Override
    @Transactional
    public void saveImages(Long userId, MultipartFile imageFiles) {
        if (imageFiles == null || imageFiles.isEmpty()) return;
        User user = userRepository.findById(userId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found by id: " + userId));
        if(user.getImage() != null){
            deleteImage(userId);
        }

        RestTemplate restTemplate = new RestTemplate();

        try {
            String fileName = System.currentTimeMillis() + "_" + imageFiles.getOriginalFilename();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("apikey", supabaseKey);
            headers.set("Authorization", "Bearer " + supabaseKey);

            HttpEntity<byte[]> entity = new HttpEntity<>(imageFiles.getBytes(), headers);

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            String url = supabaseUrl + "/object/" + bucket + "/" + encodedFileName;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image: " + fileName);
            }
            user.setImage(supabaseUrl + "/object/public/" + bucket + "/" + fileName);
            userRepository.save(user);

        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to store image: " + imageFiles.getOriginalFilename());
        }
    }
    
    @Override
    @Transactional
    public void deleteImage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found by id: " + userId));

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", supabaseKey);
            headers.set("Authorization", "Bearer " + supabaseKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String publicPrefix = supabaseUrl + "/object/public/";
            String imagePath = user.getImage().replace(publicPrefix, "");
            String deleteUrl = supabaseUrl + "/object/" + imagePath;
            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, String.class);
            user.setImage(null);
            userRepository.save(user);
        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete image: " + user.getImage());
        }
    }
    
    @Override
    public UserProfileOverviewDTO userProfileOverview(Long userId, String saleType){
        User user = userRepository.findById(userId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found by id: " + userId));
        UserProfileOverviewDTO userOverview = new UserProfileOverviewDTO();
        userOverview.setId(userId);
        userOverview.setName(user.getName());
        userOverview.setDescription(user.getDescription());
        userOverview.setImage(user.getImage());
        userOverview.setPhone(user.getPhone());
        userOverview.setLineId(user.getLineId());
        userOverview.setJoinAt(user.getCreatedAt());
        userOverview.setPhoneVerified(user.getPhoneVerified());
        userOverview.setEmailVerified(user.getEmailVerified());

        List<Announce> announceList = announceRepository.findAllByUserId(userId);
        
        userOverview.setAnnounceSellCount(announceList.stream()
                .filter(announce -> announce.getSaleType().getType().equalsIgnoreCase(SaleTypeConstant.SALE)).toList().size()
        );
        
        userOverview.setAnnounceRentCount(announceList.stream()
                .filter(announce -> announce.getSaleType().getType().equals(SaleTypeConstant.RENT)).toList().size()
        );

        String mappedSaleType;
        if(saleType.equals("เช่า")) {
            mappedSaleType = SaleTypeConstant.RENT;
        } else if(saleType.equals("ขาย")) {
            mappedSaleType = SaleTypeConstant.SALE;
        } else {
            mappedSaleType = saleType;
        }
        
        userOverview.setAnnounceList(announceList.stream().filter(announce -> announce.getSaleType().getType().equalsIgnoreCase(mappedSaleType) && announce.getUser().getId().equals(userId))
                .map(
                announce -> {
                    AnnounceByTypeDTO announceByType = new AnnounceByTypeDTO();
                    announceByType.setId(announce.getId());
                    announceByType.setTitle(announce.getTitle());
                    announceByType.setImage(
                            Optional.ofNullable(announce.getImageList())
                            .flatMap(list -> list.stream().findFirst())
                            .map(AnnounceImage::getImageUrl)
                            .orElse(null)
                    );
                    announceByType.setLocation(announce.getLocation());
                    return announceByType;
                }
            ).toList()
        );
        return userOverview;
    }
    
    @Override
    public String bookmarkAnnounce(String email, Long announceId) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
            Announce announce = announceRepository.findById(announceId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce not found."));
            user.getBookmarks().add(announce);
            userRepository.save(user);
            return "บันทึกประกาศสำเร็จ";
        }catch (WebException e) {
            throw e;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public String removeFromBookmark(String email, Long announceId) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
            Announce announce = announceRepository.findById(announceId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce not found."));
            user.getBookmarks().remove(announce);
            userRepository.save(user);
            return "ยกเลิกบันทึกประกาศสำเร็จ";
        }catch (WebException e) {
            throw e;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    public List<ShowAllAnnounceDetailsWithAgent> showAllAnnounceBookmark(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
        List<Announce> announces = user.getBookmarks().stream().toList();
        return mapToShowAllAnnounce(announces);
    }
    
    @Override
    @Transactional
    public String updateUserProfile(String email, EditProfileDTO editProfile) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
            user.setName(editProfile.getName());
            user.setDescription(editProfile.getDescription());
            
            if(!user.getPhone().equals(editProfile.getPhone())){
                user.setPhone(editProfile.getPhone());
                user.setPhoneVerified(false);
                notificationService.systemSendNotification(user, "คุณได้เปลี่ยนเบอร์โทรศัพท์ กรุณายืนยันตัวตนในหน้าโปรไฟล์", "ระบบตรวจพบว่าคุณได้ทำการเปลี่ยนเบอร์โทรศัพท์ใหม่ กรุณาดำเนินการยืนยันตัวตนในหน้าโปรไฟล์เพื่อความปลอดภัย");
            }
            
            if(!user.getEmail().equals(editProfile.getEmail())){
                user.setEmail(editProfile.getEmail());
                user.setEmailVerified(false);
                notificationService.systemSendNotification(user, "คุณได้เปลี่ยนที่อยู่อีเมล กรุณายืนยันตัวตนในหน้าโปรไฟล์", "ระบบตรวจพบว่าคุณได้ทำการเปลี่ยนที่อยู่อีเมลใหม่ กรุณาดำเนินการยืนยันตัวตนในหน้าโปรไฟล์เพื่อความปลอดภัย");
            }
            user.setLineId(editProfile.getLineId());
            userRepository.save(user);
            return "Update User details success.";
        }catch (WebException e) {
            throw e;
        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    public ShowUserDetailsDTO showUserDetails(String email) {
        try{
            return userRepository.findUserDetailsById(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
        }catch (WebException e) {
            throw e;
        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public List<ShowAllUserDTO> showAllUserSelector() {
        return userRepository.findAllUserSelector();
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

    private AgentDTO mapToAgentDTO(User agent) {
        AgentDTO agentDTO = modelMapper.map(agent, AgentDTO.class);
        agentDTO.setIsVerify(agent.getEmailVerified() && agent.getPhoneVerified());
        return agentDTO;
    }

    private Set<BadgeDTO> mapToBadgeDTO(Set<Badge> badge) {
        return badge.stream().map(bg -> modelMapper.map(bg, BadgeDTO.class)).collect(Collectors.toSet());
    }
}

