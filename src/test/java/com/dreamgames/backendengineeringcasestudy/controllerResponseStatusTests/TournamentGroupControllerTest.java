package com.dreamgames.backendengineeringcasestudy.controllerResponseStatusTests;

import com.dreamgames.backendengineeringcasestudy.controller.TournamentGroupController;
import com.dreamgames.backendengineeringcasestudy.enums.Country;
import com.dreamgames.backendengineeringcasestudy.exception.CustomExceptionHandler;
import com.dreamgames.backendengineeringcasestudy.exception.GroupNotFoundException;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponse;
import com.dreamgames.backendengineeringcasestudy.service.TournamentGroupService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class TournamentGroupControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TournamentGroupService tournamentGroupService;

    @InjectMocks
    private TournamentGroupController tournamentGroupController;

    private final CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tournamentGroupController)
                .setControllerAdvice(customExceptionHandler)
                .build();
    }

    @Test
    void should_ReturnLeaderboard_When_GetGroupLeaderboard() throws Exception {
        List<TournamentParticipantResponse> leaderboard = Collections.singletonList(new TournamentParticipantResponse(
                1L,
                0,
                Country.TURKEY
        ));
        when(tournamentGroupService.getGroupLeaderboard(anyLong())).thenReturn(leaderboard);

        mockMvc.perform(get("/tournament-groups/get-group-leaderboard/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void should_ReturnNotFound_When_GetGroupLeaderboardWithInvalidGroupId() throws Exception {
        when(tournamentGroupService.getGroupLeaderboard(anyLong())).thenThrow(new GroupNotFoundException(1L));

        mockMvc.perform(get("/tournament-groups/get-group-leaderboard/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
