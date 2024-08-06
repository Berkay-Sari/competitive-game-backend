package com.dreamgames.backendengineeringcasestudy.serviceUnitTests;

import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import com.dreamgames.backendengineeringcasestudy.enums.Country;
import com.dreamgames.backendengineeringcasestudy.exception.GroupNotFoundException;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentParticipantRepository;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponse;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponseMapper;
import com.dreamgames.backendengineeringcasestudy.service.TournamentGroupService;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class TournamentGroupServiceTest {

    @Mock
    private TournamentParticipantRepository tournamentParticipantRepository;

    @Mock
    private TournamentParticipantResponseMapper tournamentParticipantResponseMapper;

    @Mock
    private TournamentService tournamentService;

    @InjectMocks
    private TournamentGroupService tournamentGroupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void should_ReturnGroupLeaderboard_When_GroupExists() {
        TournamentParticipant participant = new TournamentParticipant();
        TournamentParticipantResponse response = new TournamentParticipantResponse(1L, 1, Country.TURKEY);
        when(tournamentParticipantRepository.findByTournamentGroupIdOrderByScoreDesc(anyLong()))
                .thenReturn(List.of(participant));
        when(tournamentParticipantResponseMapper.apply(participant)).thenReturn(response);

        List<TournamentParticipantResponse> leaderboard = tournamentGroupService.getGroupLeaderboard(1L);

        assertEquals(1, leaderboard.size());
        assertEquals(response, leaderboard.get(0));
    }

    @Test
    void should_ThrowGroupNotFoundException_When_GroupDoesNotExist() {
        when(tournamentParticipantRepository.findByTournamentGroupIdOrderByScoreDesc(anyLong()))
                .thenReturn(Collections.emptyList());

        assertThrows(GroupNotFoundException.class, () -> tournamentGroupService.getGroupLeaderboard(1L));
    }
}