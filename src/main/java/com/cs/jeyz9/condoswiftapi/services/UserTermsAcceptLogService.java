package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Terms;
import com.cs.jeyz9.condoswiftapi.models.TermsType;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.models.UserTermsAcceptLog;
import com.cs.jeyz9.condoswiftapi.repository.TermsRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserTermsAcceptLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserTermsAcceptLogService {
    private final UserTermsAcceptLogRepository acceptLogRepository;
    private final UserRepository userRepository;
    private final TermsRepository termsRepository;

    @Autowired
    public UserTermsAcceptLogService(UserTermsAcceptLogRepository acceptLogRepository, UserRepository userRepository, TermsRepository termsRepository){
        this.acceptLogRepository = acceptLogRepository;
        this.userRepository = userRepository;
        this.termsRepository = termsRepository;
    }
    
    public String userTermsAcceptLog(Long userId, HttpServletRequest request) throws WebException {
        try{
            User user = userRepository.findById(userId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found by id: " + userId));
            Terms terms = termsRepository.findByTypeAndIsActiveTrue(TermsType.AGENT_CONTACT_POLICY).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Terms not found."));
            UserTermsAcceptLog acceptLog = new UserTermsAcceptLog();
            acceptLog.setUser(user);
            acceptLog.setTerms(terms);
            acceptLog.setIpAddress(request.getRemoteAddr());
            acceptLog.setUserAgent(request.getHeader("User-Agent"));
            acceptLogRepository.save(acceptLog);
            return "Accept terms success";
        } catch (WebException e) {
            throw e;
        }catch (Exception e){
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Terms Accept fail " + e.getMessage());
        }
    }
}
