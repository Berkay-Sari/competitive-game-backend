package com.dreamgames.backendengineeringcasestudy.controllerResponseStatusTests;

import com.dreamgames.backendengineeringcasestudy.controller.TournamentController;
import com.dreamgames.backendengineeringcasestudy.enums.Country;
import com.dreamgames.backendengineeringcasestudy.exception.CustomExceptionHandler;
import com.dreamgames.backendengineeringcasestudy.exception.NoActiveTournamentException;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.response.CountryLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponse;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class TournamentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TournamentService tournamentService;

    @InjectMocks
    private TournamentController tournamentController;

    private final CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tournamentController)
                .setControllerAdvice(customExceptionHandler)
                .build();
    }

    @Test
    void should_ReturnParticipants_When_EnterTournament() throws Exception {
        List<TournamentParticipantResponse> participants = Collections.singletonList(new TournamentParticipantResponse(
                1L,
                0,
                Country.TURKEY
        ));
        when(tournamentService.enterTournament(anyLong())).thenReturn(participants);

        mockMvc.perform(post("/tournaments/enter-tournament/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void should_ReturnNotFound_When_EnterTournamentWithInvalidUserId() throws Exception {
        when(tournamentService.enterTournament(anyLong())).thenThrow(new UserNotFoundException(1L));

        mockMvc.perform(post("/tournaments/enter-tournament/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_ReturnLeaderboard_When_GetCountryLeaderboard() throws Exception {
        List<CountryLeaderboardResponse> leaderboard = Collections.singletonList(new CountryLeaderboardResponse(
                Country.FRANCE,
                1L
        ));
        when(tournamentService.getCountryLeaderboardResponse(anyLong())).thenReturn(leaderboard);

        mockMvc.perform(get("/tournaments/get-country-leaderboard/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void should_ReturnNotFound_When_GetCountryLeaderboardWithInvalidTournamentId() throws Exception {
        when(tournamentService.getCountryLeaderboardResponse(anyLong())).thenThrow(new NoActiveTournamentException());

        mockMvc.perform(get("/tournaments/get-country-leaderboard/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
