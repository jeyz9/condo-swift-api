package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.config.JwtTokenProvider;
import com.cs.jeyz9.condoswiftapi.dto.LoginDTO;
import com.cs.jeyz9.condoswiftapi.dto.RegisterDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Notification;
import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.models.RoleName;
import com.cs.jeyz9.condoswiftapi.models.Terms;
import com.cs.jeyz9.condoswiftapi.models.TermsType;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.models.UserTermsAcceptLog;
import com.cs.jeyz9.condoswiftapi.models.VerificationToken;
import com.cs.jeyz9.condoswiftapi.repository.NotificationRepository;
import com.cs.jeyz9.condoswiftapi.repository.RoleRepository;
import com.cs.jeyz9.condoswiftapi.repository.TermsRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserTermsAcceptLogRepository;
import com.cs.jeyz9.condoswiftapi.repository.VerificationTokenRepository;
import com.cs.jeyz9.condoswiftapi.services.AuthService;
import com.cs.jeyz9.condoswiftapi.services.EmailService;
import jakarta.mail.MessagingException;
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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

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
                           EmailService emailService, NotificationRepository notificationRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userTermsAcceptLogRepository = userTermsAcceptLogRepository;
        this.termsRepository = termsRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.notificationRepository = notificationRepository;
    }
    @Override
    public String register(RegisterDTO register, HttpServletRequest request) throws WebException {
        try{
            if(userRepository.existsByEmail(register.getEmail())){
                throw new WebException(HttpStatus.BAD_REQUEST, "Email already exist!");
            }

            if (!register.getPassword().equals(register.getConfirmPassword())) {
                throw new WebException(HttpStatus.BAD_REQUEST, "Password does not match");
            }
            
            if(!register.getIsAgree()){
                throw new WebException(HttpStatus.BAD_REQUEST, "You must accept the terms and conditions");
            }
    
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

            Terms terms = termsRepository.findByTypeAndIsActiveTrue(TermsType.REGISTER_TERMS)
                    .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Terms not found."));
            
            UserTermsAcceptLog termsAcceptLog = new UserTermsAcceptLog();
            termsAcceptLog.setUser(user);
            termsAcceptLog.setTerms(terms);
            termsAcceptLog.setUserAgent(request.getHeader("User-Agent"));
            termsAcceptLog.setIpAddress(request.getRemoteAddr());
            userTermsAcceptLogRepository.save(termsAcceptLog);

            if(user.getRoles().stream().anyMatch(role -> role.getRoleName().equals(RoleName.AGENT))){
                Notification notification = Notification
                        .builder()
                        .title("สมัครสมาชิกสำเร็จ")
                        .message("คุณได้สมัครบัญชีสำเร็จแล้ว กรุณาทำการยืนยันตัวตนเพื่อเปิดใช้งานเต็มรูปแบบ")
                        .is_read(false)
                        .user(user)
                        .createdDate(LocalDateTime.now())
                        .expiredDate(LocalDateTime.now().plusDays(7))
                        .build();
                notificationRepository.save(notification);
            }
            
            return "Register Success and Terms Accepted.";
        }catch (WebException e){
            throw e;
        }catch (Exception e){
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Register fails " + e.getMessage());
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

//    @Override
//    public void sendVerificationEmail(Long userId) throws MessagingException {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("ไม่พบผู้ใช้"));
//
//        if (user.getEmailVerified()) {
//            throw new RuntimeException("บัญชีนี้ได้รับการยืนยันแล้ว");
//        }
//
//        String token = UUID.randomUUID().toString();
//
//        tokenRepository.findByToken(tokenRepository.toString()).ifPresent(tokenRepository::delete);
//
//        VerificationToken verificationToken = new VerificationToken();
//        verificationToken.setToken(token);
//        verificationToken.setUser(user);
//        verificationToken.setExiryDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
//        tokenRepository.save(verificationToken);
//
//        String verificationUrl = "http://localhost:8080/api/v1/auth/verify?token=" + token;
//        String html = """
//            <html>
//              <body>
//                <h2>ยืนยันอีเมลของคุณ</h2>
//                <p>คลิกลิงก์ด้านล่างเพื่อยืนยันอีเมลของคุณ:</p>
//                <a href="%s" style="background:#4CAF50;color:#fff;padding:10px 20px;text-decoration:none;border-radius:6px;">ยืนยันอีเมล</a>
//                <p>ลิงก์นี้จะหมดอายุใน 24 ชั่วโมง</p>
//              </body>
//            </html>
//        """.formatted(verificationUrl);
//
//        emailService.sendHtmlEmail(user.getEmail(), "ยืนยันอีเมลของคุณ", html);
//    }

    @Override
    public String verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token ไม่ถูกต้อง"));

        if (verificationToken.getExiryDate().before(new Date())) {
            return "ลิงก์หมดอายุแล้ว";
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
        Notification notification = Notification
                .builder()
                .title("ยืนยันอีเมลสำเร็จ")
                .message("บัญชีของคุณได้รับการยืนยันอีเมลเรียบร้อย")
                .is_read(false)
                .user(user)
                .createdDate(LocalDateTime.now())
                .expiredDate(LocalDateTime.now().plusDays(7))
                .build();
        notificationRepository.save(notification);
        return "บัญชีของคุณได้รับการยืนยันอีเมลเรียบร้อย";
    }
    
    @Override
    public String changePassword(Long userId, String password, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
        
        return null;
    }
    private User mapToUser(RegisterDTO register) {
        return modelMapper.map(register, User.class);
    }
}
