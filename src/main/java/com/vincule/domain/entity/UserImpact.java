package com.vincule.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_impact")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class UserImpact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "total_donations", nullable = false)
    @Builder.Default
    private Integer totalDonations = 0;

    @Column(name = "total_hours", nullable = false)
    @Builder.Default
    private Integer totalHours = 0;

    @CreationTimestamp
    @Column(name = "last_activity", nullable = false)
    private LocalDateTime lastActivity;
}
