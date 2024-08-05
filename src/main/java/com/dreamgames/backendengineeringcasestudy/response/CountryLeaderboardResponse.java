package com.dreamgames.backendengineeringcasestudy.response;

import com.dreamgames.backendengineeringcasestudy.entity.Country;

public record CountryLeaderboardResponse(
        Country country,
        Long score
) {
}
