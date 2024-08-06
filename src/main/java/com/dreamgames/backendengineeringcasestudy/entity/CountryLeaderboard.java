package com.dreamgames.backendengineeringcasestudy.entity;


import com.dreamgames.backendengineeringcasestudy.enums.Country;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CountryLeaderboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Country country;

    private Long score;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;
}
