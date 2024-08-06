package com.dreamgames.backendengineeringcasestudy.serviceUnitTests;

import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.enums.Country;
import com.dreamgames.backendengineeringcasestudy.exception.*;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentGroupRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentParticipantRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.repo.UserRepository;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponse;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponseMapper;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@TestPropertySource(locations = "classpath:application-test.properties")
public class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TournamentParticipantRepository tournamentParticipantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TournamentGroupRepository tournamentGroupRepository;

    @Mock
    private TournamentParticipantResponseMapper tournamentParticipantResponseMapper;

    @InjectMocks
    private TournamentService tournamentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void should_CreateFirstTournament_When_ApplicationReadyEventDuringTournamentTime() {
        when(tournamentRepository.findByIsActiveTrue()).thenReturn(Optional.empty());
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        tournamentService.createFirstTournament();

        LocalTime currentTimeUtc = LocalTime.now(ZoneOffset.UTC);
        LocalTime targetTime = LocalTime.of(20, 0);

        if (currentTimeUtc.isAfter(targetTime)) {
            verify(tournamentRepository, times(0)).save(any(Tournament.class));
            assertNull(tournamentService.getCurrentTournament());
        } else {
            verify(tournamentRepository, times(1)).save(any(Tournament.class));
            assertEquals(tournament, tournamentService.getCurrentTournament());
        }
    }

    @Test
    void should_ThrowNoActiveTournamentException_When_EnterTournamentWithNoActiveTournament() {
        tournamentService.setCurrentTournament(null);

        assertThrows(NoActiveTournamentException.class, () -> tournamentService.enterTournament(1L));
    }

    @Test
    void should_ThrowUserAlreadyParticipantException_When_UserAlreadyInTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournamentService.setCurrentTournament(tournament);
        when(tournamentParticipantRepository.findByUserIdAndTournamentGroupTournamentId(anyLong(), anyLong()))
                .thenReturn(Optional.of(new TournamentParticipant()));

        assertThrows(UserAlreadyParticipantException.class, () -> tournamentService.enterTournament(1L));
    }

    @Test
    void should_ThrowUserNotFoundException_When_UserNotFound() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournamentService.setCurrentTournament(tournament);
        when(tournamentParticipantRepository.findByUserIdAndTournamentGroupTournamentId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> tournamentService.enterTournament(1L));
    }

    @Test
    void should_ThrowUnclaimedRewardException_When_UserHasUnclaimedReward() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setActive(true);
        tournamentService.setCurrentTournament(tournament);
        User user = new User();
        user.setId(1L);
        TournamentParticipant lastParticipation = new TournamentParticipant();
        lastParticipation.setRewardClaimed(false);
        TournamentGroup group = new TournamentGroup(tournament);
        group.setId(1L);
        lastParticipation.setTournamentGroup(group);
        List<TournamentParticipant> participations = List.of(
                lastParticipation,
                new TournamentParticipant(),
                new TournamentParticipant(),
                new TournamentParticipant(),
                new TournamentParticipant()
        );
        when(tournamentParticipantRepository.findByUserIdAndTournamentGroupTournamentId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        TournamentParticipant participant = new TournamentParticipant();
        participant.setRewardClaimed(false);
        when(tournamentParticipantRepository.findByUserIdOrderByCreatedAtDesc(anyLong()))
                .thenReturn(List.of(lastParticipation));
        when(tournamentParticipantRepository.findByTournamentGroupIdOrderByScoreDesc(anyLong()))
                .thenReturn(participations);

        assertThrows(UnclaimedRewardException.class, () -> tournamentService.enterTournament(1L));
    }

    @Test
    void should_ThrowMinimumLevelRequirementException_When_UserLevelTooLow() {
        Tournament tournament = new Tournament();
        tournamentService.setCurrentTournament(tournament);
        User user = new User();
        user.setLevel(10);
        when(tournamentParticipantRepository.findByUserIdAndTournamentGroupTournamentId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(tournamentParticipantRepository.findByUserIdOrderByCreatedAtDesc(anyLong()))
                .thenReturn(List.of(new TournamentParticipant()));

        assertThrows(MinimumLevelRequirementException.class, () -> tournamentService.enterTournament(1L));
    }

    @Test
    void should_ThrowInsufficientCoinsException_When_UserHasInsufficientCoins() {
        Tournament tournament = new Tournament();
        tournamentService.setCurrentTournament(tournament);
        User user = new User();
        user.setCoins(500);
        when(tournamentParticipantRepository.findByUserIdAndTournamentGroupTournamentId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(tournamentParticipantRepository.findByUserIdOrderByCreatedAtDesc(anyLong()))
                .thenReturn(List.of(new TournamentParticipant()));

        assertThrows(InsufficientCoinsException.class, () -> tournamentService.enterTournament(1L));
    }

    @Test
    void should_EnterTournamentSuccessfully_When_ValidUser() {
        Tournament tournament = new Tournament();
        tournamentService.setCurrentTournament(tournament);
        User user = new User();
        user.setId(1L);
        user.setCoins(2000);
        user.setLevel(30);
        when(tournamentParticipantRepository.findByUserIdAndTournamentGroupTournamentId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        TournamentGroup group = new TournamentGroup(tournament);
        group.setId(1L);
        when(tournamentGroupRepository.findGroupsWithoutUserFromCountry(anyLong(), any(Country.class), any(PageRequest.class)))
                .thenReturn(List.of(group));
        when(tournamentGroupRepository.save(any(TournamentGroup.class))).thenReturn(group);
        when(tournamentParticipantRepository.save(any(TournamentParticipant.class))).thenReturn(new TournamentParticipant());
        when(tournamentParticipantRepository.findByUserIdOrderByCreatedAtDesc(anyLong()))
                .thenReturn(List.of());
        when(tournamentParticipantRepository.findByTournamentGroupIdOrderByScoreDesc(anyLong()))
                .thenReturn(List.of(new TournamentParticipant()));
        List<TournamentParticipantResponse> responses = tournamentService.enterTournament(1L);

        assertEquals(1, responses.size());
        verify(userRepository, times(1)).save(user);
        verify(tournamentParticipantRepository, times(1)).save(any(TournamentParticipant.class));
    }
}
