package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CronJobService {

    private final TournamentService tournamentService;
    private Long currentTournamentId;

    public CronJobService(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "UTC") // At 00:00 UTC every day
    public void startTournament() {
        Tournament tournament = tournamentService.createTournament();
        currentTournamentId = tournament.getTournamentId();
    }

    @Scheduled(cron = "0 0 20 * * ?", zone = "UTC") // At 20:00 UTC every day
    public void endTournament() {
        if (currentTournamentId != null) {
            tournamentService.endTournament(currentTournamentId);
            currentTournamentId = null;
        }
    }
}