package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.response.RankResponse;
import com.dreamgames.backendengineeringcasestudy.service.TournamentParticipantService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournament-participants")
public class TournamentParticipantController {

    private final TournamentParticipantService tournamentParticipantService;

    public TournamentParticipantController(TournamentParticipantService tournamentParticipantService) {
        this.tournamentParticipantService = tournamentParticipantService;
    }

    @PostMapping("/claim-reward/{userId}")
    public User claimReward(@PathVariable Long userId) {
        return tournamentParticipantService.claimReward(userId);
    }

    @GetMapping("/get-group-rank")
    public RankResponse getGroupRank(@RequestParam Long userId, @RequestParam Long tournamentId) {
        int rank = tournamentParticipantService.getGroupRank(userId, tournamentId);
        return new RankResponse(rank);
    }
}
