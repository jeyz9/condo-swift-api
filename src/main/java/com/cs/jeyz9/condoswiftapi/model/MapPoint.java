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
@Table(name = "map_point")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MapPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String lat;
    private String lng;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announce_id", referencedColumnName = "id")
    private Announce announce;
    
    private LocalDateTime createdAt;
}
