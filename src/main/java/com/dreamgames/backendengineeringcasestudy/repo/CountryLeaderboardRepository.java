package com.dreamgames.backendengineeringcasestudy.repo;

import com.dreamgames.backendengineeringcasestudy.entity.CountryLeaderboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryLeaderboardRepository extends JpaRepository<CountryLeaderboard, Long> {
    List<CountryLeaderboard> findByTournamentId(Long tournamentId);
}
