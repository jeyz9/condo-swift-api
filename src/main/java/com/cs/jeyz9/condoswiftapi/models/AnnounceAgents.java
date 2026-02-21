package com.cs.jeyz9.condoswiftapi.models;

import jakarta.persistence.Entity;
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
@Table(name = "announce_agents")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnounceAgents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "announce_id", referencedColumnName = "id")
    private Announce announce;
    
    @ManyToOne
    @JoinColumn(name = "agent_id", referencedColumnName = "id")
    private User agent;
    
    private String permission;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    
    @ManyToOne
    @JoinColumn(name = "approve_by", referencedColumnName = "id")
    private User approvedBy;
}
