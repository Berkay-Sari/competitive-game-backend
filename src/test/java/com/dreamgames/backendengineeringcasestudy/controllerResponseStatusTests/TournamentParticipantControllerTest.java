package com.dreamgames.backendengineeringcasestudy.controllerResponseStatusTests;

import com.dreamgames.backendengineeringcasestudy.controller.TournamentParticipantController;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.exception.CustomExceptionHandler;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.service.TournamentParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class TournamentParticipantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TournamentParticipantService tournamentParticipantService;

    @InjectMocks
    private TournamentParticipantController tournamentParticipantController;

    private final CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tournamentParticipantController)
                .setControllerAdvice(customExceptionHandler)
                .build();
    }

    @Test
    void should_ReturnUser_When_ClaimReward() throws Exception {
        User user = new User();
        user.setId(1L);
        when(tournamentParticipantService.claimReward(anyLong())).thenReturn(user);

        mockMvc.perform(post("/tournament-participants/claim-reward/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void should_ReturnNotFound_When_ClaimRewardWithInvalidUserId() throws Exception {
        when(tournamentParticipantService.claimReward(anyLong())).thenThrow(new UserNotFoundException(1L));

        mockMvc.perform(post("/tournament-participants/claim-reward/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_ReturnRank_When_GetGroupRank() throws Exception {
        when(tournamentParticipantService.getGroupRank(anyLong(), anyLong())).thenReturn(1);

        mockMvc.perform(get("/tournament-participants/get-group-rank")
                        .param("userId", "1")
                        .param("tournamentId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rank").value(1));
    }

    @Test
    void should_ReturnNotFound_When_GetGroupRankWithInvalidUserId() throws Exception {
        when(tournamentParticipantService.getGroupRank(anyLong(), anyLong())).thenThrow(new UserNotFoundException(1L));

        mockMvc.perform(get("/tournament-participants/get-group-rank")
                        .param("userId", "1")
                        .param("tournamentId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
