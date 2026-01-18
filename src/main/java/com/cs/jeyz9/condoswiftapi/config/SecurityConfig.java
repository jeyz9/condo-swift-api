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
    private final CustomAuthenticationEntryPoint customAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    
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
//                                        "/api/v1/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**", 
                                        "/v3/api-docs/**", 
                                        "/api/v1/selector/showAllProvinces",
                                        "/api/v1/auth/verify",
                                        "/api/v1/selector/showAllStations",
                                        "/api/v1/selector/showAllAnnounceTypes",
                                        "/api/v1/announces/showAnnounceWithCategory",
                                        "/api/v1/announces/showAnnounceDetails/**",
                                        "/api/v1/announces/filterAnnounceWithAgent",
                                        "/api/v1/users/showUserProfileOverview/**",
                                        "/api/v1/users/showRecommendedAgents",
                                        "/api/v1/badges/showAllBadges"
                                ).permitAll()
                                
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/users/showAllAnnounceBookmark",
                                        "/api/v1/users/{userId}/deleteProfilePicture",
                                        "/api/v1/notifications/showAllNotificationSelectedByUserId/**",
                                        "/api/v1/notifications/showNotificationDetailsSelected/**"
                                ).authenticated()
                                
                                .requestMatchers(HttpMethod.POST, 
                                        "/api/v1/auth/**",
                                        "/api/v1/users/{userId}/acceptTerms",
                                        "/api/v1/stripe/webhook"
                                ).permitAll()
                                
                                .requestMatchers(HttpMethod.GET, "/api/v1/announces/showAllAnnounceDraft").hasAuthority("ROLE_" + RoleName.AGENT)
                                .requestMatchers(HttpMethod.POST, "/api/v1/announces/**", "/api/v1/stripe/create-checkout-session").hasAuthority("ROLE_" + RoleName.AGENT)
                                .requestMatchers(HttpMethod.PUT, "/api/v1/announces/editAnnounce/**").hasAuthority("ROLE_" + RoleName.AGENT)
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/announces/**").hasAuthority("ROLE_" + RoleName.AGENT)
                                
                                .requestMatchers(HttpMethod.GET, 
                                        "/api/v1/announces/showAllAnnounceApproveByAdmin", 
                                        "/api/v1/announces/showAllAnnounceHistoryByAdmin",
                                        "/api/v1/announces/showAllAnnouncePendingByAdmin",
                                        "/api/v1/announces/showAllAnnounceBadges",
                                        "/api/v1/users/showAllUserSelector",
                                        "/api/v1/badges/filterBadges"
                                ).hasAnyAuthority("ROLE_" + RoleName.ADMIN, "ROLE_" + RoleName.MODERATOR)
                                
                                .requestMatchers(HttpMethod.POST, 
                                        "/api/v1/notifications/sendNotification",
                                        "/api/v1/badges/**"
                                ).hasAnyAuthority("ROLE_" + RoleName.ADMIN, "ROLE_" + RoleName.MODERATOR)
                                
                                .requestMatchers(HttpMethod.PUT,
                                        "/api/v1/badges/updatedBadge/**",
                                        "/api/v1/announces/approveAnnounce/**",
                                        "/api/v1/announces/rejectAnnounce/**"
                                ).hasAnyAuthority("ROLE_" + RoleName.ADMIN, "ROLE_" + RoleName.MODERATOR)
                                
                                .requestMatchers(HttpMethod.DELETE, 
                                        "/api/v1/badges/deletedBadge/**",
                                        "/api/v1/notifications/deleteNotification/**"
                                ).hasAnyAuthority("ROLE_" + RoleName.ADMIN, "ROLE_" + RoleName.MODERATOR)

                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/users/showAllUser",
                                        "/api/v1/selector/showAllRoles"
                                ).hasAuthority("ROLE_" + RoleName.ADMIN)
                                
                                .requestMatchers(HttpMethod.POST,
                                        "/api/v1/users/addUserRole"
                                ).hasAuthority("ROLE_" + RoleName.ADMIN)

                                .requestMatchers(HttpMethod.DELETE,
                                        "/api/v1/users/deleteUserRole"
                                ).hasAuthority("ROLE_" + RoleName.ADMIN)
                                
                                .anyRequest().authenticated()
                ).exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtEntryPoint)
                        .authenticationEntryPoint(customAuthEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                ).sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
