package com.dreamgames.backendengineeringcasestudy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TournamentParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private TournamentGroup tournamentGroup;

    private int score = 0;

    private boolean rewardClaimed = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public TournamentParticipant(User user, TournamentGroup tournamentGroup) {
        this.user = user;
        this.tournamentGroup = tournamentGroup;
    }
}
