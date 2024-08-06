package com.dreamgames.backendengineeringcasestudy.repoUnitTests;

import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.enums.Country;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentGroupRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentParticipantRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.repo.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TournamentGroupRepositoryTest {

    @Autowired
    private TournamentGroupRepository tournamentGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentParticipantRepository tournamentParticipantRepository;

    private Tournament tournament;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setCountry(Country.TURKEY);
        userRepository.save(user);

        tournament = new Tournament();
        tournamentRepository.save(tournament);
    }

    @AfterEach
    public void tearDown() {
        tournamentParticipantRepository.deleteAll();
        tournamentGroupRepository.deleteAll();
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Test
    public void shouldFindGroupsWithoutUserFromCountry_WhenGroupsExist() {
        TournamentGroup group1 = new TournamentGroup(tournament);
        TournamentGroup group2 = new TournamentGroup(tournament);
        tournamentGroupRepository.save(group1);
        tournamentGroupRepository.save(group2);

        TournamentParticipant participant = new TournamentParticipant(user, group1);
        tournamentParticipantRepository.save(participant);

        Pageable pageable = PageRequest.of(0, 10);
        List<TournamentGroup> groups = tournamentGroupRepository.findGroupsWithoutUserFromCountry(tournament.getId(), Country.TURKEY, pageable);

        assertThat(groups).hasSize(1);
        assertThat(groups.get(0)).isEqualTo(group2);
    }

    @Test
    public void shouldFindGroupsWithoutUserFromCountry_WhenNoGroupsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TournamentGroup> groups = tournamentGroupRepository.findGroupsWithoutUserFromCountry(tournament.getId(), Country.TURKEY, pageable);

        assertThat(groups).isEmpty();
    }

    @Test
    public void shouldFindByTournamentId_WhenGroupsExist() {
        TournamentGroup group1 = new TournamentGroup(tournament);
        TournamentGroup group2 = new TournamentGroup(tournament);
        tournamentGroupRepository.save(group1);
        tournamentGroupRepository.save(group2);

        List<TournamentGroup> groups = tournamentGroupRepository.findByTournamentId(tournament.getId());

        assertThat(groups).hasSize(2);
        assertThat(groups).containsExactlyInAnyOrder(group1, group2);
    }

    @Test
    public void shouldFindByTournamentId_WhenNoGroupsExist() {
        List<TournamentGroup> groups = tournamentGroupRepository.findByTournamentId(tournament.getId());

        assertThat(groups).isEmpty();
    }
}
