package com.dreamgames.backendengineeringcasestudy.repoUnitTests;

import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TournamentRepositoryTest {

    @Autowired
    private TournamentRepository tournamentRepository;

    @AfterEach
    public void tearDown() {
        tournamentRepository.deleteAll();
    }

    @Test
    public void shouldFindActiveTournament_WhenActiveTournamentExists() {
        Tournament activeTournament = new Tournament();
        activeTournament.setActive(true);
        tournamentRepository.save(activeTournament);

        Optional<Tournament> foundTournament = tournamentRepository.findByIsActiveTrue();

        assertThat(foundTournament).isPresent();
        assertThat(foundTournament.get().isActive()).isTrue();
    }

    @Test
    public void shouldNotFindActiveTournament_WhenNoActiveTournamentExists() {
        Tournament inactiveTournament = new Tournament();
        inactiveTournament.setActive(false);
        tournamentRepository.save(inactiveTournament);

        Optional<Tournament> foundTournament = tournamentRepository.findByIsActiveTrue();

        assertThat(foundTournament).isNotPresent();
    }
}
