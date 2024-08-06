package com.dreamgames.backendengineeringcasestudy.response;

import com.dreamgames.backendengineeringcasestudy.enums.Country;

public record TournamentParticipantResponse(
        Long userId,
        int score,
        Country country
) {
}
