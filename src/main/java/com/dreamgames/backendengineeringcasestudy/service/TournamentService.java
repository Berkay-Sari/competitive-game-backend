package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponse;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponseMapper;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipant;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentGroupRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentParticipantRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.repo.UserRepository;
import lombok.Getter;
import org.hibernate.ObjectNotFoundException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final UserRepository userRepository;
    private final TournamentGroupRepository tournamentGroupRepository;
    private final TournamentParticipantResponseMapper tournamentParticipantResponseMapper;

    @Getter
    private Tournament currentTournament;

    public TournamentService(TournamentRepository tournamentRepository,
                             TournamentParticipantRepository tournamentParticipantRepository,
                             UserRepository userRepository,
                             TournamentGroupRepository tournamentGroupRepository, TournamentParticipantResponseMapper tournamentParticipantResponseMapper) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentParticipantRepository = tournamentParticipantRepository;
        this.userRepository = userRepository;
        this.tournamentGroupRepository = tournamentGroupRepository;
        this.tournamentParticipantResponseMapper = tournamentParticipantResponseMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createFirstTournament() {
        LocalTime currentTimeUtc = LocalTime.now(ZoneOffset.UTC);
        LocalTime targetTime = LocalTime.of(20, 0);

        if (currentTimeUtc.isAfter(targetTime)) {
            return;
        }

        Tournament tournament = tournamentRepository.findCurrentTournament().orElse(new Tournament());
        currentTournament = tournamentRepository.save(tournament);
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "UTC")
    public void createTournament() {
        currentTournament = tournamentRepository.save(new Tournament());
    }

    @Scheduled(cron = "0 0 20 * * ?", zone = "UTC")
    public void endTournament() {
        if (currentTournament != null) {
            currentTournament.setActive(false);
            tournamentRepository.save(currentTournament);
            currentTournament = null;
        }
    }

    public List<TournamentParticipantResponse> enterTournament(Long userId) {
        if (currentTournament == null) {
            throw new IllegalArgumentException("No active tournament");
        }

        boolean isParticipant = tournamentParticipantRepository.findByUserIdAndTournamentGroupTournamentId(userId,
                currentTournament.getId()).isPresent();

        if (isParticipant) {
            throw new IllegalArgumentException("User is already a participant");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(User.class.getName(), userId));

        validateTournamentEligibility(user);

        user.setCoins(user.getCoins() - 1000);
        user = userRepository.save(user);

        TournamentGroup tournamentGroup = tournamentGroupRepository
                .findGroupWithoutUserFromCountry(currentTournament.getId(), user.getCountry())
                .orElse(new TournamentGroup(currentTournament));

        if (!tournamentGroup.isActive() &&
                tournamentParticipantRepository.countByTournamentGroupId(tournamentGroup.getId()) == 4) {
            tournamentGroup.setActive(true);
        }

        tournamentGroup = tournamentGroupRepository.save(tournamentGroup);

        tournamentParticipantRepository.save(new TournamentParticipant(user, tournamentGroup));

        List<TournamentParticipant> participantsByGroupIdOrderedByScore = tournamentParticipantRepository.findParticipantsByGroupIdOrderedByScore(tournamentGroup.getId());
        return participantsByGroupIdOrderedByScore.stream().map(tournamentParticipantResponseMapper).toList();
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
