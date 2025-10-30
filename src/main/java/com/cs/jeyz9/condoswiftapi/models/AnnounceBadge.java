package com.cs.jeyz9.condoswiftapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "announce_badge")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnounceBadge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "announce_id")
    @JsonIgnore
    private Announce announce;

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;

    private LocalDateTime assignedAt = LocalDateTime.now();

    private LocalDateTime expiresAt;
}
