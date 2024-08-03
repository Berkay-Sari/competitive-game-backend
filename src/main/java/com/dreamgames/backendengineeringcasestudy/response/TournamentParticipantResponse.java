package com.dreamgames.backendengineeringcasestudy.response;

import com.dreamgames.backendengineeringcasestudy.entity.Country;

public record TournamentParticipantResponse(
        Long userId,
        int score,
        Country country
) {
}
