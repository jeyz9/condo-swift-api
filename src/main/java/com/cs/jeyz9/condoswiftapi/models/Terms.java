package com.cs.jeyz9.condoswiftapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "terms")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Terms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String version;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TermsType type;
    
    @JsonProperty("is_active")
    @Column(nullable = false)
    private Boolean isActive;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    public Terms(String version, String content, TermsType type, Boolean isActive){
        this.version = version;
        this.content = content;
        this.type = type;
        this.isActive = isActive;
        this.createdAt = LocalDateTime.now();
    }
}
