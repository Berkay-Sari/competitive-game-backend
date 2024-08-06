package com.dreamgames.backendengineeringcasestudy.repo;

import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipant, Long> {

    Optional<TournamentParticipant> findByUserIdAndTournamentGroupTournamentId(Long userId, Long tournamentId);

    Long countByTournamentGroupId(Long groupId);

    List<TournamentParticipant> findByTournamentGroupIdOrderByScoreDesc(Long groupId);

    List<TournamentParticipant> findByUserIdOrderByCreatedAtDesc(Long id);
}

