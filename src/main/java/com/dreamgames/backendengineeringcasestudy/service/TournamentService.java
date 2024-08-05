package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.*;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponse;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponseMapper;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentGroupRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentParticipantRepository;
import com.dreamgames.backendengineeringcasestudy.repo.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.repo.UserRepository;
import lombok.Getter;
import org.hibernate.ObjectNotFoundException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
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

    public final static int TOURNAMENT_ENTRY_FEE = 1000;
    public final static int MINIMUM_LEVEL = 20;
    public final static int FIRST_PLACE_REWARD = 10000;
    public final static int SECOND_PLACE_REWARD = 5000;

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

        Tournament tournament = tournamentRepository.findByIsActiveTrue().orElse(new Tournament());
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

        boolean isParticipant = tournamentParticipantRepository
                .findByUserIdAndTournamentGroupTournamentId(userId, currentTournament.getId()).isPresent();

        if (isParticipant) {
            throw new IllegalArgumentException("User is already a participant");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(User.class.getName(), userId));

        validateTournamentEligibility(user);

        Long tournamentId = currentTournament.getId();
        Country userCountry = user.getCountry();
        TournamentGroup tournamentGroup = findMostFullyGroupWithoutUserFromCountry(tournamentId, userCountry);

        if (!tournamentGroup.isActive() &&
                tournamentParticipantRepository.countByTournamentGroupId(tournamentGroup.getId()) == 4) {
            tournamentGroup.setActive(true);
        }

        tournamentGroup = tournamentGroupRepository.save(tournamentGroup);

        user.setCoins(user.getCoins() - TOURNAMENT_ENTRY_FEE);
        user = userRepository.save(user);

        tournamentParticipantRepository.save(new TournamentParticipant(user, tournamentGroup));

        List<TournamentParticipant> participantsByGroupIdOrderedByScore = tournamentParticipantRepository
                .findByTournamentGroupIdOrderByScoreDesc(tournamentGroup.getId());
        return participantsByGroupIdOrderedByScore.stream().map(tournamentParticipantResponseMapper).toList();
    }

    private void validateTournamentEligibility(User user) {
        List<TournamentParticipant> participations = tournamentParticipantRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());

        if (!participations.isEmpty() && !participations.get(0).isRewardClaimed()) {
            throw new IllegalArgumentException("User has unclaimed rewards from the last tournament");
        }

        if (user.getLevel() < MINIMUM_LEVEL) {
            throw new IllegalArgumentException("Must be at least " + MINIMUM_LEVEL + "level");
        }

        if (user.getCoins() < TOURNAMENT_ENTRY_FEE) {
            throw new IllegalArgumentException("Must have " + TOURNAMENT_ENTRY_FEE +" coins");
        }
    }

    private TournamentGroup findMostFullyGroupWithoutUserFromCountry(Long tournamentId, Country country) {
        List<TournamentGroup> groups = tournamentGroupRepository
                .findGroupsWithoutUserFromCountry(tournamentId, country, PageRequest.of(0, 1));

        if (!groups.isEmpty()) {
            return groups.get(0);
        } else {
            return new TournamentGroup(currentTournament);
        }
    }
}
