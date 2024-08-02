package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping("/enter-tournament/{userId}")
    public List<TournamentParticipant> enterTournament(@PathVariable Long userId) {
        return tournamentService.enterTournament(userId);
    }





}
