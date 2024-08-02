package com.dreamgames.backendengineeringcasestudy.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TournamentParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private TournamentGroup tournamentGroup;

    private int score = 0;

    private boolean rewardClaimed = false;

    public TournamentParticipant(User user, TournamentGroup tournamentGroup) {
        this.user = user;
        this.tournamentGroup = tournamentGroup;
    }
}
