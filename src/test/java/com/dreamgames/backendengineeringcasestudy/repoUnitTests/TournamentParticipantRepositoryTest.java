package com.dreamgames.backendengineeringcasestudy.repoUnitTests;

import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentGroupRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentParticipantRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.repo.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TournamentParticipantRepositoryTest {

    @Autowired
    private TournamentParticipantRepository tournamentParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TournamentGroupRepository tournamentGroupRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    private User user;
    private TournamentGroup tournamentGroup;

    @BeforeEach
    public void setUp() {
        user = new User();
        userRepository.save(user);
        Tournament tournament = new Tournament();
        tournamentRepository.save(tournament);
        tournamentGroup = new TournamentGroup(tournament);
        tournamentGroupRepository.save(tournamentGroup);
    }

    @AfterEach
    public void tearDown() {
        tournamentParticipantRepository.deleteAll();
        userRepository.deleteAll();
        tournamentGroupRepository.deleteAll();
    }

    @Test
    public void shouldReturnParticipant_WhenFindByUserIdAndTournamentGroupTournamentId() {
        TournamentParticipant participant = new TournamentParticipant(user, tournamentGroup);
        tournamentParticipantRepository.save(participant);

        Optional<TournamentParticipant> foundParticipant = tournamentParticipantRepository
                .findByUserIdAndTournamentGroupTournamentId(
                        user.getId(),
                        tournamentGroup.getTournament().getId()
                );

        assertThat(foundParticipant).isPresent();
        assertThat(foundParticipant.get().getUser()).isEqualTo(user);
        assertThat(foundParticipant.get().getTournamentGroup()).isEqualTo(tournamentGroup);
    }

    @Test
    public void shouldReturnCorrectCount_WhenCountByTournamentGroupId() {
        TournamentParticipant participant1 = new TournamentParticipant(user, tournamentGroup);
        TournamentParticipant participant2 = new TournamentParticipant(user, tournamentGroup);
        tournamentParticipantRepository.save(participant1);
        tournamentParticipantRepository.save(participant2);

        Long count = tournamentParticipantRepository.countByTournamentGroupId(tournamentGroup.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    public void shouldReturnParticipantsOrderedByScoreDesc_WhenFindByTournamentGroupIdOrderByScoreDesc() {
        TournamentParticipant participant1 = new TournamentParticipant(user, tournamentGroup);
        participant1.setScore(100);
        TournamentParticipant participant2 = new TournamentParticipant(user, tournamentGroup);
        participant2.setScore(200);
        tournamentParticipantRepository.save(participant1);
        tournamentParticipantRepository.save(participant2);

        List<TournamentParticipant> participants = tournamentParticipantRepository
                .findByTournamentGroupIdOrderByScoreDesc(tournamentGroup.getId());

        assertThat(participants).hasSize(2);
        assertThat(participants.get(0).getScore()).isGreaterThanOrEqualTo(participants.get(1).getScore());
    }

    @Test
    public void shouldReturnParticipantsOrderedByCreatedAtDesc_WhenFindByUserIdOrderByCreatedAtDesc() {
        TournamentParticipant participant1 = new TournamentParticipant(user, tournamentGroup);
        participant1.setCreatedAt(LocalDateTime.now().minusDays(1));
        TournamentParticipant participant2 = new TournamentParticipant(user, tournamentGroup);
        participant2.setCreatedAt(LocalDateTime.now());
        tournamentParticipantRepository.save(participant1);
        tournamentParticipantRepository.save(participant2);

        List<TournamentParticipant> participants = tournamentParticipantRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());

        assertThat(participants).hasSize(2);
        assertThat(participants.get(0).getCreatedAt()).isAfterOrEqualTo(participants.get(1).getCreatedAt());
    }
}
