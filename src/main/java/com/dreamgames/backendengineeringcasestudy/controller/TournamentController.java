package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.response.CountryLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponse;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping("/enter-tournament/{userId}")
    public List<TournamentParticipantResponse> enterTournament(@PathVariable Long userId) {
        return tournamentService.enterTournament(userId);
    }

    @GetMapping("/get-country-leaderboard/{tournamentId}")
    public List<CountryLeaderboardResponse> getCountryLeaderboard(@PathVariable Long tournamentId) {
        return tournamentService.getCountryLeaderboardResponse(tournamentId);
    }





}
