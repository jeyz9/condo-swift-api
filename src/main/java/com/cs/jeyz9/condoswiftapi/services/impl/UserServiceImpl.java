package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.constants.AnnounceTypeConstant;
import com.cs.jeyz9.condoswiftapi.constants.SaleTypeConstant;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceByTypeDTO;
import com.cs.jeyz9.condoswiftapi.dto.RecommendedAgenDTO;
import com.cs.jeyz9.condoswiftapi.dto.UserProfileOverviewDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Announce;
import com.cs.jeyz9.condoswiftapi.models.AnnounceImage;
import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.models.RoleName;
import com.cs.jeyz9.condoswiftapi.models.Terms;
import com.cs.jeyz9.condoswiftapi.models.TermsType;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.models.UserTermsAcceptLog;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceRepository;
import com.cs.jeyz9.condoswiftapi.repository.RoleRepository;
import com.cs.jeyz9.condoswiftapi.repository.SaleTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.TermsRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserTermsAcceptLogRepository;
import com.cs.jeyz9.condoswiftapi.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TermsRepository termsRepository;
    private final UserTermsAcceptLogRepository userTermsAcceptLogRepository;
    private final RoleRepository roleRepository;
    private final AnnounceRepository announceRepository;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket.profile}")
    private String bucket;

    public UserServiceImpl(UserRepository userRepository, TermsRepository termsRepository, UserTermsAcceptLogRepository userTermsAcceptLogRepository, RoleRepository roleRepository, AnnounceRepository announceRepository) {
        this.userRepository = userRepository;
        this.termsRepository = termsRepository;
        this.userTermsAcceptLogRepository = userTermsAcceptLogRepository;
        this.roleRepository = roleRepository;
        this.announceRepository = announceRepository;
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
                .filter(agen -> agen.getEmailVerified() && agen.getPhoneVerified())
                .map(agen -> {
                    RecommendedAgenDTO recommendedAgenDTO = new RecommendedAgenDTO();
                    recommendedAgenDTO.setId(agen.getId());
                    recommendedAgenDTO.setName(agen.getName());
                    recommendedAgenDTO.setDescription(agen.getDescription());
                    recommendedAgenDTO.setImage(agen.getImage());
                    recommendedAgenDTO.setIsVerified(agen.getEmailVerified() && agen.getPhoneVerified());    
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

            String url = supabaseUrl + "/object/" + bucket + "/" + fileName;
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
        userOverview.setJoinAt(user.getCreatedAt());

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
}

