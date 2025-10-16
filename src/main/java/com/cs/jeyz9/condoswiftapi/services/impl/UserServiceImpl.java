package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.dto.RecommendedAgenDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.models.RoleName;
import com.cs.jeyz9.condoswiftapi.models.Terms;
import com.cs.jeyz9.condoswiftapi.models.TermsType;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.models.UserTermsAcceptLog;
import com.cs.jeyz9.condoswiftapi.repository.RoleRepository;
import com.cs.jeyz9.condoswiftapi.repository.TermsRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserTermsAcceptLogRepository;
import com.cs.jeyz9.condoswiftapi.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TermsRepository termsRepository;
    private final UserTermsAcceptLogRepository userTermsAcceptLogRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, TermsRepository termsRepository, UserTermsAcceptLogRepository userTermsAcceptLogRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.termsRepository = termsRepository;
        this.userTermsAcceptLogRepository = userTermsAcceptLogRepository;
        this.roleRepository = roleRepository;
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
}

