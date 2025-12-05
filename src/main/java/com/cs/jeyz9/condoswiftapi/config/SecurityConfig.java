package com.cs.jeyz9.condoswiftapi.config;

import com.cs.jeyz9.condoswiftapi.exceptions.CustomAccessDeniedHandler;
import com.cs.jeyz9.condoswiftapi.exceptions.CustomAuthenticationEntryPoint;
import com.cs.jeyz9.condoswiftapi.models.RoleName;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtEntryPoint;
    private CustomAuthenticationEntryPoint customAuthEntryPoint;
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    
    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtFilter, JwtAuthenticationEntryPoint jwtEntryPoint, CustomAuthenticationEntryPoint customAuthEntryPoint, CustomAccessDeniedHandler customAccessDeniedHandler){
        this.jwtFilter = jwtFilter;
        this.jwtEntryPoint = jwtEntryPoint;
        this.customAuthEntryPoint = customAuthEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }
    
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) -> 
                        authorize
                                .requestMatchers(HttpMethod.GET, 
                                        "/api/v1/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**", 
                                        "/v3/api-docs/**", 
                                        "/api/v1/provinces/showAllProvinces",
                                        "/api/v1/auth/verify"
                                ).permitAll()
                                
                                .requestMatchers(HttpMethod.POST, 
                                        "/api/v1/auth/**",
                                        "/api/v1/users/{userId}/acceptTerms"
                                ).permitAll()
                                
                                .requestMatchers(HttpMethod.POST, "/api/v1/announces/**").hasRole(RoleName.AGENT.toString())
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/announces/**").hasRole(RoleName.AGENT.toString())
                                
                                .requestMatchers(HttpMethod.GET, 
                                        "/api/v1/announces/showAllAnnounceApproveByAdmin", 
                                        "/api/v1/announces/showAllAnnounceHistoryByAdmin",
                                        "/api/v1/announces/showAllAnnouncePendingByAdmin",
                                        "/api/v1/badges/filterBadges"
                                ).hasRole(RoleName.ADMIN.toString())
                                
                                .requestMatchers(HttpMethod.POST, 
                                        "/api/v1/notifications/**", 
                                        "/api/v1/badges/**"
                                ).hasRole(RoleName.ADMIN.toString())
                                
                                .requestMatchers(HttpMethod.PUT,  
                                        "/api/v1/badges/updatedBadge/**",
                                        "/api/v1/announces/approveAnnounce/**",
                                        "/api/v1/announces/rejectAnnounce/**"
                                ).hasRole(RoleName.ADMIN.toString())
                                
                                .requestMatchers(HttpMethod.DELETE, 
                                        "/api/v1/badges/deletedBadge/**",
                                        "/api/v1/notifications/deleteNotification/**"
                                ).hasRole(RoleName.ADMIN.toString())
                                
                                .anyRequest().authenticated()
                ).exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtEntryPoint)
                ).sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        http.exceptionHandling(e -> e
                .authenticationEntryPoint(customAuthEntryPoint) // 401
                .accessDeniedHandler(customAccessDeniedHandler));
        return http.build();
    }
}
