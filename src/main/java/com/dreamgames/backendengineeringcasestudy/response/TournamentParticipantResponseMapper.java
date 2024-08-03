package com.dreamgames.backendengineeringcasestudy.response;

import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class TournamentParticipantResponseMapper implements Function<TournamentParticipant, TournamentParticipantResponse> {
    @Override
    public TournamentParticipantResponse apply(TournamentParticipant tournamentParticipant) {
        return new TournamentParticipantResponse(
                tournamentParticipant.getUser().getId(),
                tournamentParticipant.getScore(),
                tournamentParticipant.getUser().getCountry()
        );
    }
}
