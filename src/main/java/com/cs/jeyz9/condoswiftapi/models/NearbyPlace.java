package com.cs.jeyz9.condoswiftapi.models;

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

@Getter
@Setter
@Entity
@Table(name = "nearby_places")
@NoArgsConstructor
@AllArgsConstructor
public class NearbyPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Enumerated(EnumType.STRING)
    private NearbyPlaceTypes type;
    
    private String image;
    
    public NearbyPlace(String name, NearbyPlaceTypes type) {
        this.name = name;
        this.type = type;
    }
}
