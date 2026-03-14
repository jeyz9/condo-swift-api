package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.AgentDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceByAgentRequestDTO;
import com.cs.jeyz9.condoswiftapi.dto.ApproveAgentRequestDTO;
import com.cs.jeyz9.condoswiftapi.dto.ShowAllManageAnnounceDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Announce;
import com.cs.jeyz9.condoswiftapi.models.AnnounceAgent;
import com.cs.jeyz9.condoswiftapi.models.AnnounceImage;
import com.cs.jeyz9.condoswiftapi.models.PermissionState;
import com.cs.jeyz9.condoswiftapi.models.RequestAnnounceStatus;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceAgentRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceStateApproveRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.ProvinceRepository;
import com.cs.jeyz9.condoswiftapi.repository.SaleTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnounceAgentService {
    private final AnnounceAgentRepository announceAgentRepository;
    private final UserRepository userRepository;
    private final AnnounceRepository announceRepository;
    private final AnnounceImageService announceImageService;

    @Autowired
    public AnnounceAgentService(AnnounceAgentRepository announceAgentRepository, UserRepository userRepository, AnnounceRepository announceRepository, ProvinceRepository provinceRepository, AnnounceStateApproveRepository announceStateApproveRepository, AnnounceTypeRepository announceTypeRepository, SaleTypeRepository saleTypeRepository, AnnounceImageService announceImageService) {
        this.announceAgentRepository = announceAgentRepository;
        this.userRepository = userRepository;
        this.announceRepository = announceRepository;
        this.announceImageService = announceImageService;
    }
    
    public String requestToManageAnnounce(String email, Long announceId) {
        try{
            User user = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found"));
            Announce announce = announceRepository.findById(announceId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce not found"));
            if(announceAgentRepository.checkAgentRequestAnnounce(announce.getId(), user.getId()) != 0) {
                throw new WebException(HttpStatus.BAD_REQUEST, "Request this announce already exist.");
            }
            
            AnnounceAgent announceAgent = AnnounceAgent.builder()
                    .id(null)
                    .announce(announce)
                    .agent(user)
                    .status(RequestAnnounceStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();
            announceAgentRepository.save(announceAgent);
            return "Send request success.";
        }catch (WebException e){
            throw e;   
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error " + e.getMessage());
        }
    };
    
    @Transactional
    public String cancelManageRequest(String email, Long announceAgentId) {
        try{
            User agent = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found"));
            AnnounceAgent announceAgent = announceAgentRepository.findById(announceAgentId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce Agent not found"));
            announceAgentRepository.deleteAnnounceAgentsByIdAndAgentId(announceAgent.getId(), agent.getId());
            return "Delete request successfully";
        }catch (WebException e){
            throw e;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error " + e.getMessage());
        }
    }
    
    // Agent
    public List<ShowAllManageAnnounceDTO> getMyManagedAnnounces(String email){
        try {
            User agent = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found"));
            List<AnnounceAgent> announceAgents = announceAgentRepository.findAllByAgentId(agent.getId());
            return mapToShowAllManageAnnounceDTO(announceAgents);
        }catch (WebException e){
            throw e;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error " + e.getMessage());
        }
    }
    
    @Transactional
    public String updateAnnounceByAgent(Long announceAgentId, AnnounceByAgentRequestDTO announceDTO, List<MultipartFile> imageFiles, String email){
        try {
            AnnounceAgent announceAgent = announceAgentRepository.findById(announceAgentId)
                    .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce agent not found"));

            Announce announce = announceRepository.findById(announceAgent.getAnnounce().getId())
                    .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce not found"));

            User agent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST,
                            "User not found"));
            
            if(!(announceAgent.getAgent().getId().equals(agent.getId()) && announceAgent.getPermission().equals(PermissionState.EDIT_CONTENT))) throw new WebException(HttpStatus.FORBIDDEN, "You are not have permission of this announcement");

            announce.setTitle(announceDTO.getTitle());
            announce.setBathroomCount(announceDTO.getBathroomCount());
            announce.setBedroomCount(announceDTO.getBedroomCount());
            announce.setAreaSize(announceDTO.getAreaSize());
            announce.setHasPool(announceDTO.getHasPool());
            announce.setHasParking(announceDTO.getHasParking());
            announce.setHasFitness(announceDTO.getHasFitness());
            announce.setHasElevator(announceDTO.getHasElevator());
            announce.setHasSecurity(announceDTO.getHasSecurity());
            announce.setHasConvenienceStore(announceDTO.getHasConvenienceStore());
            announce.setUpdatedBy(agent);
            announce.setUpdatedAt(LocalDateTime.now());
            
            Announce saved = announceRepository.save(announce);
            if (imageFiles != null && !imageFiles.isEmpty()) {
                announceImageService.saveImages(saved.getId(), imageFiles);
            }

            return "Update announce success";

        } catch (WebException e) {
            throw e;
        } catch (Exception e) {
            throw new WebException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Update \"Announce\" fails: " + e.getMessage()
            );
        }
    }
    
    // Owner
    public List<AgentDTO> getAgentsByAnnounce(String email, Long announceId){
        try {
            User owner = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Owner not found"));
            Announce announce = announceRepository.findById(announceId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce not found"));
            if (!announce.getUser().getId().equals(owner.getId())) throw new WebException(HttpStatus.FORBIDDEN, "You are not the owner of this announcement");
            
            return announceAgentRepository.findAnnounceAgentByAnnounceId(announce.getId());
        }catch (WebException e) {
            throw e;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    public String approveAgent(String email, Long approveAgentId, ApproveAgentRequestDTO request){
        try{
            User owner = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Owner not found"));
            AnnounceAgent announceAgent = announceAgentRepository.findById(approveAgentId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce Agent not found"));
            if (!announceAgent.getAnnounce().getUser().getId().equals(owner.getId())) throw new WebException(HttpStatus.FORBIDDEN, "You are not the owner of this announcement");
            
            announceAgent.setApprovedAt(LocalDateTime.now());
            announceAgent.setApprovedBy(owner);
            announceAgent.setPermission(request.getPermission());
            announceAgent.setStatus(RequestAnnounceStatus.APPROVED);
            announceAgentRepository.save(announceAgent);
            return "Approve agent success";
        }catch (WebException e) {
            throw e;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error " + e.getMessage());
        }
    }
    
    public String revokeAgent(String email, Long approveAgentId){
        try{
            AnnounceAgent announceAgent = announceAgentRepository.findById(approveAgentId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce Agent not found"));
            User owner = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Owner not found"));
            if (!announceAgent.getAnnounce().getUser().getId().equals(owner.getId())) throw new WebException(HttpStatus.FORBIDDEN, "You are not the owner of this announcement");

            announceAgent.setApprovedAt(LocalDateTime.now());
            announceAgent.setApprovedBy(owner);
            announceAgent.setStatus(RequestAnnounceStatus.REVOKED);
            announceAgentRepository.save(announceAgent);
            return "Revoke agent success";
        }catch (WebException e) {
            throw e;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error " + e.getMessage());
        }
    }
    
    public String updateAgentPermission(String email, Long approveAgentId, ApproveAgentRequestDTO request){
        try{
            User owner = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Owner not found"));
            AnnounceAgent announceAgent = announceAgentRepository.findById(approveAgentId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Announce Agent not found"));
            if(!owner.getId().equals(announceAgent.getAnnounce().getUser().getId())) throw new WebException(HttpStatus.FORBIDDEN, "You are not the owner of this announcement");

            announceAgent.setPermission(request.getPermission());
            announceAgentRepository.save(announceAgent);
            return "Approve agent success";
        }catch (WebException e) {
            throw e;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error " + e.getMessage());
        }
    }
    
    private List<ShowAllManageAnnounceDTO> mapToShowAllManageAnnounceDTO(List<AnnounceAgent> announceAgents) {
        return announceAgents.stream().map(a -> 
            ShowAllManageAnnounceDTO.builder()
                    .id(a.getId())
                    .announceName(a.getAnnounce().getTitle())
                    .announceImage(a.getAnnounce().getImageList().stream().findFirst().map(AnnounceImage::getImageUrl).orElse(null))
                    .status(a.getStatus().toString())
                    .permission(a.getPermission().toString())
                    .build()
        ).toList();
    }
}
