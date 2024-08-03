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
        TournamentParticipant tournamentParticipant = tournamentParticipantRepository
                .findUnclaimedRewardForInactiveTournamentByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No unclaimed reward found for the user"));

        if (tournamentParticipant.getTournamentGroup().getTournament().isActive()) {
            throw new IllegalArgumentException("Tournament is still active");
        }

        if (tournamentParticipant.isRewardClaimed()) {
            throw new IllegalArgumentException("Reward already claimed");
        }

        List<TournamentParticipant> participants = tournamentParticipantRepository
                .findParticipantsByGroupIdOrderedByScore(tournamentParticipant.getTournamentGroup().getId());

        int rank = participants.indexOf(tournamentParticipant) + 1;
        int reward = 0;

        if (rank == 1) {
            reward = 10000;
        } else if (rank == 2) {
            reward = 5000;
        }

        if (reward > 0) {
            synchronized (userId) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ObjectNotFoundException(User.class.getName(), userId));
                user.setCoins(user.getCoins() + reward);
                userRepository.save(user);
                tournamentParticipant.setRewardClaimed(true);
                tournamentParticipantRepository.save(tournamentParticipant);
                return user;
            }
        } else {
            throw new IllegalArgumentException("No reward for the given rank");
        }
    }

    public int getGroupRank(Long userId, Long tournamentId) {
        TournamentParticipant tournamentParticipant = tournamentParticipantRepository
                .findByUserIdAndTournamentGroupTournamentId(userId, tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("No participation found for the given tournament and user"));

        List<TournamentParticipant> participants = tournamentParticipantRepository
                .findParticipantsByGroupIdOrderedByScore(tournamentParticipant.getTournamentGroup().getId());

        return participants.indexOf(tournamentParticipant) + 1;
    }

}
