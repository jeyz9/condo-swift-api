package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.config.JwtTokenProvider;
import com.cs.jeyz9.condoswiftapi.dto.ChangePasswordDTO;
import com.cs.jeyz9.condoswiftapi.dto.LoginDTO;
import com.cs.jeyz9.condoswiftapi.dto.RegisterDTO;
import com.cs.jeyz9.condoswiftapi.dto.ResetPasswordDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.PasswordResetToken;
import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.models.RoleName;
import com.cs.jeyz9.condoswiftapi.models.Terms;
import com.cs.jeyz9.condoswiftapi.models.TermsType;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.models.UserTermsAcceptLog;
import com.cs.jeyz9.condoswiftapi.models.VerificationToken;
import com.cs.jeyz9.condoswiftapi.repository.PasswordResetTokenRepository;
import com.cs.jeyz9.condoswiftapi.repository.RoleRepository;
import com.cs.jeyz9.condoswiftapi.repository.TermsRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserTermsAcceptLogRepository;
import com.cs.jeyz9.condoswiftapi.repository.VerificationTokenRepository;
import com.cs.jeyz9.condoswiftapi.services.AuthService;
import com.cs.jeyz9.condoswiftapi.services.BlacklistTokenService;
import com.cs.jeyz9.condoswiftapi.services.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserTermsAcceptLogRepository userTermsAcceptLogRepository;
    private final TermsRepository termsRepository;
    private final VerificationTokenRepository tokenRepository;
    private final BlacklistTokenService blacklistTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final NotificationService notificationService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           UserTermsAcceptLogRepository userTermsAcceptLogRepository,
                           TermsRepository termsRepository,
                           VerificationTokenRepository tokenRepository,
                           BlacklistTokenService blacklistTokenService, 
                           PasswordResetTokenRepository passwordResetTokenRepository, 
                           NotificationService notificationService){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userTermsAcceptLogRepository = userTermsAcceptLogRepository;
        this.termsRepository = termsRepository;
        this.tokenRepository = tokenRepository;
        this.blacklistTokenService = blacklistTokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.notificationService = notificationService;
    }
    
    @Override
    @Transactional
    public String register(RegisterDTO register, HttpServletRequest request) throws WebException {
        try{
            if(userRepository.existsByEmail(register.getEmail())){
                throw new WebException(HttpStatus.BAD_REQUEST, "อีเมลนี้มีผู้ใช้แล้ว");
            }
            
            if (userRepository.existsByPhone(register.getPhone())) {
                throw new WebException(HttpStatus.BAD_REQUEST, "เบอร์โทรนี้มีผู้ใช้แล้ว");
            }

            if (!register.getPassword().equals(register.getConfirmPassword())) {
                throw new WebException(HttpStatus.BAD_REQUEST, "Password does not match");
            }
            
            if(!register.getIsAgree()){
                throw new WebException(HttpStatus.BAD_REQUEST, "You must accept the terms and conditions");
            }

            Terms terms = termsRepository.findByTypeAndIsActiveTrue(TermsType.REGISTER_TERMS)
                    .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Terms not found."));
    
            User user = mapToUser(register);
            user.setName(register.getName());
            user.setDescription(register.getDescription());
            user.setPhone(register.getPhone());
            user.setEmail(register.getEmail());
            user.setPassword(passwordEncoder.encode(register.getPassword()));
            
            Set<Role> roles = new HashSet<>();
            Role agenRole = roleRepository.findByRoleName(RoleName.AGENT).orElseThrow(() -> new IllegalArgumentException("Role not found"));
            Role userRole = roleRepository.findByRoleName(RoleName.USER).orElseThrow(() -> new IllegalArgumentException("Role not found"));
            if(register.getIsAgent()){
                roles.add(agenRole);
            }else{
                roles.add(userRole);
            }
            user.setRoles(roles);
            userRepository.save(user);
            
            UserTermsAcceptLog termsAcceptLog = new UserTermsAcceptLog();
            termsAcceptLog.setUser(user);
            termsAcceptLog.setTerms(terms);
            termsAcceptLog.setUserAgent(request.getHeader("User-Agent"));
            termsAcceptLog.setIpAddress(request.getRemoteAddr());
            userTermsAcceptLogRepository.save(termsAcceptLog);

            notificationService.systemSendNotification(user, "สมัครสมาชิกสำเร็จ", "คุณได้สมัครบัญชีสำเร็จแล้ว กรุณาทำการยืนยันตัวตนเพื่อเปิดใช้งานเต็มรูปแบบ");
            
            return "Register Success and Terms Accepted.";
        }catch (WebException e){
            throw e;
        }catch (Exception e){
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "เกิดข้อผิดพลาดทางเซิร์ฟเวอร์");
        }
    }
    
    @Override
    public String login(LoginDTO login) throws WebException {
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    login.getEmail(), login.getPassword()
            ));
    
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtTokenProvider.generateToken(authentication);
        } catch (AuthenticationException e) {
            throw new WebException(HttpStatus.UNAUTHORIZED ,"อีเมลหรือรหัสผ่านไม่ถูกต้อง");
        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "เกิดข้อผิดพลาดภายในระบบ: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public String verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token ไม่ถูกต้อง"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "ลิงก์หมดอายุแล้ว";
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
        notificationService.systemSendNotification(user, "ยืนยันอีเมลสำเร็จ", "บัญชีของคุณได้รับการยืนยันอีเมลเรียบร้อย");
        return "บัญชีของคุณได้รับการยืนยันอีเมลเรียบร้อย";
    }
    
    @Override
    @Transactional
    public String changePassword(String email, ChangePasswordDTO request, String token) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
        
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            throw new WebException(HttpStatus.BAD_REQUEST, "Old password is incorrect.");
        }
        
        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new WebException(HttpStatus.BAD_REQUEST, "New password and confirm password do not match");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        if (token != null) {
            LocalDateTime expiry = jwtTokenProvider.extractExpiration(token)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            blacklistTokenService.blacklist(token, expiry);
        }
        
        userRepository.save(user);
        
        return "Change password success!";
    }

    @Override
    @Transactional
    public String resetPassword(String token, ResetPasswordDTO request){
        try {
            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Token not found."));
            User user = userRepository.findByEmail(resetToken.getEmail()).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));

            if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw new WebException(HttpStatus.BAD_REQUEST, "Reset token expired");
            }
            
            if(!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new WebException(HttpStatus.BAD_REQUEST, "New password and confirm password do not match");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            passwordResetTokenRepository.delete(resetToken);
            return "Reset password success.";
        }catch (WebException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    private User mapToUser(RegisterDTO register) {
        return modelMapper.map(register, User.class);
    }
}
