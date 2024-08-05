package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.Country;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentParticipantRepository;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponse;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponseMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

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

        return participants.stream()
                .map(tournamentParticipantResponseMapper)
                .toList();
    }

    public Map<Country, Integer> getCountryLeaderboard(Long tournamentId) {
        List<TournamentParticipant> participants = tournamentParticipantRepository
                .findByTournamentGroupTournamentId(tournamentId);

        Map<Country, Integer> countryScores = new HashMap<>();
        for (TournamentParticipant participant : participants) {
            Country country = participant.getUser().getCountry();
            countryScores.merge(country, participant.getScore(), Integer::sum);
        }

        return countryScores.entrySet().stream()
                .sorted(Map.Entry.<Country, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }


}
