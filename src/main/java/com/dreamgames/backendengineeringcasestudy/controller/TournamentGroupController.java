package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponse;
import com.dreamgames.backendengineeringcasestudy.service.TournamentGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournament-groups")
public class TournamentGroupController {

    private final TournamentGroupService tournamentGroupService;

    public TournamentGroupController(TournamentGroupService tournamentGroupService) {
        this.tournamentGroupService = tournamentGroupService;
    }

    @GetMapping("/get-group-leaderboard/{groupId}")
    List<TournamentParticipantResponse> getGroupLeaderboard(@PathVariable Long groupId) {
        return tournamentGroupService.getGroupLeaderboard(groupId);
    }
}
