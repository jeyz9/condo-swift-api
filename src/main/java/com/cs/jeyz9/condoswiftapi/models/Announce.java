package com.cs.jeyz9.condoswiftapi.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "announces")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Announce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String location;
    private Double price;
    private Integer bathroomCount;
    private Integer bedroomCount;
    private Integer areaSize;
    private Boolean hasPool;
    private Boolean hasConvenienceStore;
    private Boolean hasFitness;
    private Boolean hasElevator;
    private Boolean hasParking;
    private Boolean hasSecurity;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approve_id", referencedColumnName = "id")
    private AnnounceStateApprove approve;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approve_by", referencedColumnName = "id")
    private User approveBy;
    
    @OneToMany(mappedBy = "announce", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<AnnounceBadge> announceBadges = new HashSet<>();
    
    @OneToMany(mappedBy = "announce", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MapPoint> mapPointList = new ArrayList<>();
    
    @OneToMany(mappedBy = "announce", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AnnounceImage> imageList;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private AnnounceType announceType;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private SaleType saleType;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Province province;
    
    private String remark;
    
    private LocalDateTime approveDate;
    
    private LocalDateTime announcementDate = LocalDateTime.now();
    
}
