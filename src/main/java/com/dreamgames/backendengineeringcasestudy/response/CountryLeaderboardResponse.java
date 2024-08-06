package com.dreamgames.backendengineeringcasestudy.response;

import com.dreamgames.backendengineeringcasestudy.enums.Country;

public record CountryLeaderboardResponse(
        Country country,
        Long score
) {
}
