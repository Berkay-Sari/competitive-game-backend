package com.dreamgames.backendengineeringcasestudy.repoUnitTests;

import com.dreamgames.backendengineeringcasestudy.entity.CountryLeaderboard;
import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.enums.Country;
import com.dreamgames.backendengineeringcasestudy.repo.CountryLeaderboardRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CountryLeaderboardRepositoryTest {

    @Autowired
    private CountryLeaderboardRepository repository;

    @Autowired
    private TournamentRepository tournamentRepository;

    private Tournament tournament;

    @BeforeEach
    public void setUp() {
        tournament = new Tournament();
        tournamentRepository.save(tournament);
    }

    @Test
    public void shouldReturnLeaderboards_WhenTournamentHasLeaderboards() {
        CountryLeaderboard leaderboard1 = new CountryLeaderboard();
        leaderboard1.setCountry(Country.TURKEY);
        leaderboard1.setScore(100L);
        leaderboard1.setTournament(tournament);
        repository.save(leaderboard1);

        CountryLeaderboard leaderboard2 = new CountryLeaderboard();
        leaderboard2.setCountry(Country.UNITED_STATES);
        leaderboard2.setScore(200L);
        leaderboard2.setTournament(tournament);
        repository.save(leaderboard2);

        List<CountryLeaderboard> leaderboards = repository.findByTournamentId(tournament.getId());

        assertThat(leaderboards).hasSize(2);
        assertThat(leaderboards).extracting(CountryLeaderboard::getCountry)
                .containsExactlyInAnyOrder(Country.TURKEY, Country.UNITED_STATES);
        assertThat(leaderboards).extracting(CountryLeaderboard::getScore)
                .containsExactlyInAnyOrder(100L, 200L);
    }

    @Test
    public void shouldReturnEmptyList_WhenTournamentHasNoLeaderboards() {
        Long nonExistentTournamentId = 999L;

        List<CountryLeaderboard> leaderboards = repository.findByTournamentId(nonExistentTournamentId);

        assertThat(leaderboards).isEmpty();
    }

}
