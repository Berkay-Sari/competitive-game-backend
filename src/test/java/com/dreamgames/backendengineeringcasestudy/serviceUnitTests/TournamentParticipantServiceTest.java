package com.dreamgames.backendengineeringcasestudy.serviceUnitTests;

import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.exception.*;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentParticipantRepository;
import com.dreamgames.backendengineeringcasestudy.repo.UserRepository;
import com.dreamgames.backendengineeringcasestudy.service.TournamentParticipantService;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TournamentParticipantServiceTest {

    @Mock
    private TournamentParticipantRepository tournamentParticipantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TournamentService tournamentService;

    @InjectMocks
    private TournamentParticipantService tournamentParticipantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void should_IncreaseUserScore_When_ValidUserAndActiveTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setActive(true);
        tournamentService.setCurrentTournament(tournament);
        User user = new User();
        user.setId(1L);
        TournamentParticipant participant = new TournamentParticipant();
        participant.setScore(10);
        when(tournamentParticipantRepository.findByUserIdAndTournamentGroupTournamentId(user.getId(), tournament.getId()))
                .thenReturn(Optional.of(participant));
        when(tournamentService.getCurrentTournament()).thenReturn(tournament);

        tournamentParticipantService.increaseUserScore(user);

        assertEquals(11, participant.getScore());
        verify(tournamentParticipantRepository, times(1)).save(participant);
    }

    @Test
    void should_ThrowNoActiveTournamentException_When_NoActiveTournament() {
        tournamentService.setCurrentTournament(null);
        User user = new User();
        user.setId(1L);

        assertThrows(NoActiveTournamentException.class, () -> tournamentParticipantService.increaseUserScore(user));
    }

    @Test
    void should_ReturnFalse_When_NoActiveTournamentForUser() {
        tournamentService.setCurrentTournament(null);
        User user = new User();
        user.setId(1L);

        boolean result = tournamentParticipantService.hasCompetitionBeginForUser(user);

        assertFalse(result);
    }

    @Test
    void should_ReturnTrue_When_ActiveTournamentForUser() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournamentService.setCurrentTournament(tournament);
        User user = new User();
        user.setId(1L);
        TournamentParticipant participant = new TournamentParticipant();
        TournamentGroup group = new TournamentGroup();
        group.setActive(true);
        participant.setTournamentGroup(group);
        when(tournamentParticipantRepository.findByUserIdAndTournamentGroupTournamentId(user.getId(), tournament.getId()))
                .thenReturn(Optional.of(participant));
        when(tournamentService.getCurrentTournament()).thenReturn(tournament);

        boolean result = tournamentParticipantService.hasCompetitionBeginForUser(user);

        assertTrue(result);
    }

    @Test
    void should_ThrowUserNeverParticipatedException_When_UserNeverParticipated() {
        User user = new User();
        user.setId(1L);
        when(tournamentParticipantRepository.findByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(Collections.emptyList());

        assertThrows(UserNeverParticipatedException.class, () -> tournamentParticipantService.claimReward(user.getId()));
    }

    @Test
    void should_ThrowRewardAlreadyClaimedException_When_RewardAlreadyClaimed() {
        User user = new User();
        user.setId(1L);
        TournamentParticipant participant = new TournamentParticipant();
        participant.setRewardClaimed(true);
        when(tournamentParticipantRepository.findByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(List.of(participant));

        assertThrows(RewardAlreadyClaimedException.class, () -> tournamentParticipantService.claimReward(user.getId()));
    }

    @Test
    void should_ThrowNotEnoughParticipantsException_When_NotEnoughParticipants() {
        User user = new User();
        user.setId(1L);
        TournamentParticipant participant = new TournamentParticipant();
        participant.setRewardClaimed(false);
        TournamentGroup group = new TournamentGroup();
        participant.setTournamentGroup(group);
        when(tournamentParticipantRepository.findByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(List.of(participant));
        when(tournamentParticipantRepository.findByTournamentGroupIdOrderByScoreDesc(group.getId()))
                .thenReturn(List.of(participant));

        assertThrows(NotEnoughParticipantsException.class, () -> tournamentParticipantService.claimReward(user.getId()));
    }

    @Test
    void should_ClaimRewardSuccessfully_When_ValidUserAndRank() {
        User user = new User();
        user.setId(1L);
        user.setCoins(100);
        TournamentParticipant participant = new TournamentParticipant();
        participant.setRewardClaimed(false);
        TournamentGroup group = new TournamentGroup();
        participant.setTournamentGroup(group);
        when(tournamentParticipantRepository.findByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(List.of(participant));
        when(tournamentParticipantRepository.findByTournamentGroupIdOrderByScoreDesc(group.getId()))
                .thenReturn(List.of(
                        participant,
                        new TournamentParticipant(),
                        new TournamentParticipant(),
                        new TournamentParticipant(),
                        new TournamentParticipant()
                ));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User updatedUser = tournamentParticipantService.claimReward(user.getId());

        assertEquals(10100, updatedUser.getCoins());
        assertTrue(participant.isRewardClaimed());
        verify(userRepository, times(1)).save(user);
        verify(tournamentParticipantRepository, times(1)).save(participant);
    }

    @Test
    void should_ReturnCorrectGroupRank_When_ValidUserAndTournament() {
        User user = new User();
        user.setId(1L);
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        TournamentParticipant participant = new TournamentParticipant();
        participant.setUser(user);
        TournamentGroup group = new TournamentGroup();
        participant.setTournamentGroup(group);
        when(tournamentParticipantRepository.findByUserIdAndTournamentGroupTournamentId(user.getId(), tournament.getId()))
                .thenReturn(Optional.of(participant));
        when(tournamentParticipantRepository.findByTournamentGroupIdOrderByScoreDesc(group.getId()))
                .thenReturn(List.of(participant, new TournamentParticipant()));

        int rank = tournamentParticipantService.getGroupRank(user.getId(), tournament.getId());

        assertEquals(1, rank);
    }
}