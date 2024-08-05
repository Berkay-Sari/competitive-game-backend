package com.dreamgames.backendengineeringcasestudy.repo;

import com.dreamgames.backendengineeringcasestudy.entity.Country;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TournamentGroupRepository extends JpaRepository<TournamentGroup, Long> {
    @Query("SELECT g FROM TournamentGroup g " +
            "WHERE g.tournament.id = :tournamentId " +
            "AND NOT EXISTS (SELECT participant " +
                            "FROM TournamentParticipant participant " +
                            "WHERE participant.tournamentGroup = g AND participant.user.country = :country)" +
            "ORDER BY       (SELECT COUNT(participant) " +
                            "FROM TournamentParticipant participant " +
                            "WHERE participant.tournamentGroup = g) DESC")
    List<TournamentGroup> findGroupsWithoutUserFromCountry(Long tournamentId, Country country, Pageable pageable);

}

