package com.dreamgames.backendengineeringcasestudy.repo;

import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipant, Long> {
    @Query("SELECT participant FROM TournamentParticipant participant " +
            "WHERE participant.user.id = :userId " +
            "AND participant.rewardClaimed = false " +
            "AND participant.tournamentGroup.tournament.isActive = false")
    Optional<TournamentParticipant> findUnclaimedRewardForInactiveTournamentByUserId(@Param("userId") Long userId);

    @Query("SELECT participant FROM TournamentParticipant participant " +
            "WHERE participant.user.id = :userId AND participant.tournamentGroup.tournament.tournamentId = :tournamentId")
    Optional<TournamentParticipant> findParticipantByUserIdAndTournamentId(@Param("userId") Long userId,
                                                                           @Param("tournamentId") Long tournamentId);

    Long countByTournamentGroupGroupId(Long groupId);

    @Query("SELECT participants " +
            "FROM TournamentParticipant participants " +
            "WHERE participants.tournamentGroup.groupId = :groupId " +
            "ORDER BY participants.score DESC")
    List<TournamentParticipant> findParticipantsByGroupIdOrderedByScore(Long groupId);


}

