package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentParticipantRepository;
import com.dreamgames.backendengineeringcasestudy.repo.UserRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentParticipantService {
    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final UserRepository userRepository;
    private final TournamentService tournamentService;

    public TournamentParticipantService(TournamentParticipantRepository tournamentParticipantRepository,
                                        UserRepository userRepository,
                                        TournamentService tournamentService) {
        this.tournamentParticipantRepository = tournamentParticipantRepository;
        this.userRepository = userRepository;
        this.tournamentService = tournamentService;
    }

    public void increaseUserScore(User user) {
        if (tournamentService.getCurrentTournament() == null) {
            throw new IllegalArgumentException("No active tournament");
        }

        synchronized (user.getId()) {
            TournamentParticipant tournamentParticipant = tournamentParticipantRepository
                    .findByUserIdAndTournamentGroupTournamentId(user.getId(), tournamentService.getCurrentTournament().getId())
                    .orElseThrow(() -> new ObjectNotFoundException(TournamentParticipant.class.getName(), user.getId()));
            tournamentParticipant.setScore(tournamentParticipant.getScore() + 1);
            tournamentService.incrementCountryScore(user.getCountry());
            tournamentParticipantRepository.save(tournamentParticipant);
        }
    }

    public boolean hasCompetitionBeginForUser(User user) {
        if (tournamentService.getCurrentTournament() == null) {
            return false;
        }

        return tournamentParticipantRepository
                .findByUserIdAndTournamentGroupTournamentId(user.getId(), tournamentService.getCurrentTournament().getId())
                .map(TournamentParticipant::getTournamentGroup)
                .map(TournamentGroup::isActive)
                .orElse(false);
    }

    public User claimReward(Long userId) {
        if (tournamentService.getCurrentTournament() != null) {
            boolean isParticipant = tournamentParticipantRepository
                    .findByUserIdAndTournamentGroupTournamentId(userId, tournamentService.getCurrentTournament().getId())
                    .isPresent();

            if (isParticipant) {
                throw new IllegalArgumentException("User has no unclaimed rewards");
            }
        }

        List<TournamentParticipant> participations = tournamentParticipantRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        if (participations.isEmpty()) {
            throw new IllegalArgumentException("User never participated in a tournament");
        }

        TournamentParticipant lastParticipation = participations.get(0);

        if(lastParticipation.isRewardClaimed()) {
            throw new IllegalArgumentException("User has already claimed rewards");
        }

        List<TournamentParticipant> participants = tournamentParticipantRepository
                .findByTournamentGroupIdOrderByScoreDesc(lastParticipation.getTournamentGroup().getId());

        int rank = participants.indexOf(lastParticipation) + 1;
        int reward = 0;

        if (rank == 1) {
            reward = TournamentService.FIRST_PLACE_REWARD;
        } else if (rank == 2) {
            reward = TournamentService.SECOND_PLACE_REWARD;
        }

        if (reward > 0) {
            synchronized (userId) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ObjectNotFoundException(User.class.getName(), userId));
                user.setCoins(user.getCoins() + reward);
                userRepository.save(user);
                lastParticipation.setRewardClaimed(true);
                tournamentParticipantRepository.save(lastParticipation);
                return user;
            }
        } else {
            throw new IllegalArgumentException("No reward for the rank: " + rank);
        }
    }

    public int getGroupRank(Long userId, Long tournamentId) {
        TournamentParticipant tournamentParticipant = tournamentParticipantRepository
                .findByUserIdAndTournamentGroupTournamentId(userId, tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("No participation found"));

        List<TournamentParticipant> participants = tournamentParticipantRepository
                .findByTournamentGroupIdOrderByScoreDesc(tournamentParticipant.getTournamentGroup().getId());

        return participants.indexOf(tournamentParticipant) + 1;
    }

}
