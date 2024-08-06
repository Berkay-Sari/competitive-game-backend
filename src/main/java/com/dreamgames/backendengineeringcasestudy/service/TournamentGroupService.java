package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import com.dreamgames.backendengineeringcasestudy.exception.GroupNotFoundException;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentParticipantRepository;
import com.dreamgames.backendengineeringcasestudy.response.CountryLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponse;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentGroupService {
    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final TournamentParticipantResponseMapper tournamentParticipantResponseMapper;
    private final TournamentService tournamentService;

    public TournamentGroupService(TournamentParticipantRepository tournamentParticipantRepository,
                                  TournamentParticipantResponseMapper tournamentParticipantResponseMapper,
                                  TournamentService tournamentService) {
        this.tournamentParticipantRepository = tournamentParticipantRepository;
        this.tournamentParticipantResponseMapper = tournamentParticipantResponseMapper;
        this.tournamentService = tournamentService;
    }

    public List<TournamentParticipantResponse> getGroupLeaderboard(Long groupId) {
        List<TournamentParticipant> participants = tournamentParticipantRepository
                .findByTournamentGroupIdOrderByScoreDesc(groupId);

        if (participants.isEmpty()) {
            throw new GroupNotFoundException(groupId);
        }

        return participants.stream()
                .map(tournamentParticipantResponseMapper)
                .toList();
    }

    public List<CountryLeaderboardResponse> getCountryLeaderboard(Long tournamentId) {
        return tournamentService.getCountryLeaderboardResponse(tournamentId);
    }


}
