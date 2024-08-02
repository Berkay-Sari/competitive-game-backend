package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentGroupRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentParticipantRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.repo.UserRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final UserRepository userRepository;
    private final TournamentGroupRepository tournamentGroupRepository;

    public TournamentService(TournamentRepository tournamentRepository,
                             TournamentParticipantRepository userTournamentGroupRepository,
                             UserRepository userRepository,
                             TournamentGroupRepository tournamentGroupRepository) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentParticipantRepository = userTournamentGroupRepository;
        this.userRepository = userRepository;
        this.tournamentGroupRepository = tournamentGroupRepository;
    }

    public Tournament createTournament() {
        return tournamentRepository.save(new Tournament());
    }

    public void endTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ObjectNotFoundException(Tournament.class.getName(), tournamentId));

        tournament.setActive(false);
        tournamentRepository.save(tournament);
    }

    public List<TournamentParticipant> enterTournament(Long userId) {
        Tournament currentTournament = tournamentRepository.findCurrentTournament()
                .orElseThrow(() -> new ObjectNotFoundException(Optional
                        .of(Tournament.class.getName()), "No active tournament"));

        boolean isParticipant = tournamentParticipantRepository.findParticipantByUserIdAndTournamentId(userId,
                currentTournament.getTournamentId()).isPresent();

        if (isParticipant) {
            throw new IllegalArgumentException("User is already a participant");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(User.class.getName(), userId));

        validateTournamentEligibility(user);

        user.setCoins(user.getCoins() - 1000);
        user = userRepository.save(user);

        TournamentGroup tournamentGroup = tournamentGroupRepository
                .findGroupWithoutUserFromCountry(currentTournament.getTournamentId(), user.getCountry())
                .orElse(new TournamentGroup(currentTournament));

        if (!tournamentGroup.isActive() && tournamentParticipantRepository.countByTournamentGroupGroupId(tournamentGroup.getGroupId()) == 5) {
            tournamentGroup.setActive(true);
        }

        tournamentGroup = tournamentGroupRepository.save(tournamentGroup);

        tournamentParticipantRepository.save(new TournamentParticipant(user, tournamentGroup));

        return tournamentParticipantRepository.findParticipantsByGroupIdOrderedByScore(tournamentGroup.getGroupId());
    }

    public void validateTournamentEligibility(User user) {

        if (user.getLevel() < 20) {
            throw new IllegalArgumentException("Must be at least level 20");
        }

        if (user.getCoins() < 1000) {
            throw new IllegalArgumentException("Must have 1,000 coins");
        }

        boolean hasUnclaimedReward = tournamentParticipantRepository
                .findUnclaimedRewardForInactiveTournamentByUserId(user.getId())
                .isPresent();

        if (hasUnclaimedReward) {
            throw new IllegalArgumentException("Must claim last tournament reward");
        }
    }

}
