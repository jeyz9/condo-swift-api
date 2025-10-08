package com.cs.jeyz9.condoswiftapi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private String description;
    
    private String image;
    
    private String phone;
    
    private String email;
    
    private String password;
    
    private Boolean phoneVerified;
    
    private Boolean emailVerified;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();
    private LocalDateTime createdAt;
    
    public User() {}
    public User(String name, String description, String image, String phone, String email, String password, Boolean phoneVerified, Boolean emailVerified, Set<Role> roles){
        this.name = name;
        this.description = description;
        this.image = image;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.phoneVerified = phoneVerified;
        this.emailVerified = emailVerified;
        this.roles = roles;
        this.createdAt = LocalDateTime.now();
    }
    
    
}
