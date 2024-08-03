package com.dreamgames.backendengineeringcasestudy.entity;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TournamentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isActive = false;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    public TournamentGroup(Tournament tournament) {
        this.tournament = tournament;
    }
}