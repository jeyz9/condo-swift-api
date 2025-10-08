package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.config.JwtTokenProvider;
import com.cs.jeyz9.condoswiftapi.dto.LoginDTO;
import com.cs.jeyz9.condoswiftapi.dto.RegisterDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.models.RoleName;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.repository.RoleRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.services.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    
    @Autowired
    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @Override
    public String register(RegisterDTO register) {
        if(userRepository.existsByEmail(register.getEmail())){
            throw new WebException(HttpStatus.BAD_REQUEST, "Username already exist!");
        }

        User user = mapToUser(register);
        user.setName(register.getName());
        user.setDescription(register.getDescription());
        user.setImage(register.getImage());
        user.setPhone(register.getPhone());
        user.setEmail(register.getEmail());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setPhoneVerified(register.getPhoneVerified());
        user.setEmailVerified(register.getEmailVerified());

        Set<Role> roles = new HashSet<>();
        Role agenRole = roleRepository.findByRoleName(RoleName.AGEN).orElseThrow(() -> new IllegalArgumentException("Role not found"));
        Role userRole = roleRepository.findByRoleName(RoleName.USER).orElseThrow(() -> new IllegalArgumentException("Role not found"));
        if(register.getIsAgen()){
            roles.add(agenRole);
        }else{
            roles.add(userRole);
        }
        user.setRoles(roles);
        userRepository.save(user);
        
        return "User registered successfully!";
    }
    
    @Override
    public String login(LoginDTO login) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                login.getEmail(), login.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }
    
    private User mapToUser(RegisterDTO register) {
        return modelMapper.map(register, User.class);
    }
}
