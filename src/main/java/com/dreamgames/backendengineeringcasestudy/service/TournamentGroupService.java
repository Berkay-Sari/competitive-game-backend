package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import com.dreamgames.backendengineeringcasestudy.exception.GroupNotFoundException;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentParticipantRepository;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponse;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentGroupService {
    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final TournamentParticipantResponseMapper tournamentParticipantResponseMapper;

    public TournamentGroupService(TournamentParticipantRepository tournamentParticipantRepository,
                                  TournamentParticipantResponseMapper tournamentParticipantResponseMapper) {
        this.tournamentParticipantRepository = tournamentParticipantRepository;
        this.tournamentParticipantResponseMapper = tournamentParticipantResponseMapper;
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
}
