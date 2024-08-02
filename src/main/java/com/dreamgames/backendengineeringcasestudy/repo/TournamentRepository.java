package com.dreamgames.backendengineeringcasestudy.repo;

import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    @Query("SELECT t FROM Tournament t WHERE t.isActive =true")
    Optional<Tournament> findCurrentTournament();
}
