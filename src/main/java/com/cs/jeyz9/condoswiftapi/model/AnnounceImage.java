package com.cs.jeyz9.condoswiftapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "announce_images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnounceImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String imageName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announce_id", referencedColumnName = "id")
    private Announce announce;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime expireDate;
}
