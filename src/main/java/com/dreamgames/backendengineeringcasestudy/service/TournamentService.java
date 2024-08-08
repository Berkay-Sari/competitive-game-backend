package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.*;
import com.dreamgames.backendengineeringcasestudy.enums.Country;
import com.dreamgames.backendengineeringcasestudy.exception.*;
import com.dreamgames.backendengineeringcasestudy.repo.*;
import com.dreamgames.backendengineeringcasestudy.response.CountryLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponse;
import com.dreamgames.backendengineeringcasestudy.response.TournamentParticipantResponseMapper;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final UserRepository userRepository;
    private final TournamentGroupRepository tournamentGroupRepository;
    private final CountryLeaderboardRepository countryLeaderboardRepository;
    private final TournamentParticipantResponseMapper tournamentParticipantResponseMapper;

    @Getter
    @Setter
    private Tournament currentTournament;
    private final ConcurrentHashMap<Country, Long> countryLeaderboard = new ConcurrentHashMap<>();

    public final static int TOURNAMENT_ENTRY_FEE = 1000;
    public final static int MINIMUM_LEVEL = 20;
    public final static int FIRST_PLACE_REWARD = 10000;
    public final static int SECOND_PLACE_REWARD = 5000;

    @Value("${DATA_FILE_PATH:/appdata/countryLeaderboard.dat}")
    private String countryLeaderboardFilePath;

    public TournamentService(TournamentRepository tournamentRepository,
                             TournamentParticipantRepository tournamentParticipantRepository,
                             UserRepository userRepository,
                             TournamentGroupRepository tournamentGroupRepository,
                             TournamentParticipantResponseMapper tournamentParticipantResponseMapper,
                             CountryLeaderboardRepository countryLeaderboardRepository) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentParticipantRepository = tournamentParticipantRepository;
        this.userRepository = userRepository;
        this.tournamentGroupRepository = tournamentGroupRepository;
        this.tournamentParticipantResponseMapper = tournamentParticipantResponseMapper;
        this.countryLeaderboardRepository = countryLeaderboardRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createFirstTournament() {
        loadCountryLeaderboardFromFile();
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
            saveCountryLeaderboard(currentTournament);
            countryLeaderboard.forEach((country, score) -> countryLeaderboard.put(country, 0L));
            currentTournament.setActive(false);
            tournamentGroupRepository.findByTournamentId(currentTournament.getId())
                    .forEach(tournamentGroup -> {
                        tournamentGroup.setActive(false);
                        tournamentGroupRepository.save(tournamentGroup);
                    });
            tournamentRepository.save(currentTournament);
            currentTournament = null;
        }
    }

    public List<TournamentParticipantResponse> enterTournament(Long userId) {
        if (currentTournament == null) {
            throw new NoActiveTournamentException();
        }

        synchronized (userId) {
            boolean isParticipant = tournamentParticipantRepository
                    .findByUserIdAndTournamentGroupTournamentId(userId, currentTournament.getId()).isPresent();

            if (isParticipant) {
                throw new UserAlreadyParticipantException();
            }

            User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

            validateTournamentEligibility(user);

            Long tournamentId = currentTournament.getId();
            Country userCountry = user.getCountry();
            TournamentGroup tournamentGroup;

            synchronized (userCountry) {
                tournamentGroup = findMostFullyGroupWithoutUserFromCountry(tournamentId, userCountry);

                if (!tournamentGroup.isActive() &&
                        tournamentParticipantRepository.countByTournamentGroupId(tournamentGroup.getId()) == 4) {
                    tournamentGroup.setActive(true);
                }

                tournamentGroup = tournamentGroupRepository.save(tournamentGroup);

                user.setCoins(user.getCoins() - TOURNAMENT_ENTRY_FEE);
                user = userRepository.save(user);

                tournamentParticipantRepository.save(new TournamentParticipant(user, tournamentGroup));
            }

            List<TournamentParticipant> participantsByGroupIdOrderedByScore = tournamentParticipantRepository
                    .findByTournamentGroupIdOrderByScoreDesc(tournamentGroup.getId());
            return participantsByGroupIdOrderedByScore.stream().map(tournamentParticipantResponseMapper).toList();
        }
    }

    private void validateTournamentEligibility(User user) {
        if (user.getLevel() < MINIMUM_LEVEL) {
            throw new MinimumLevelRequirementException(MINIMUM_LEVEL);
        }

        if (user.getCoins() < TOURNAMENT_ENTRY_FEE) {
            throw new InsufficientCoinsException(TOURNAMENT_ENTRY_FEE);
        }

        List<TournamentParticipant> participations = tournamentParticipantRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());

        if (participations.isEmpty()) {
            return;
        }

        TournamentParticipant lastParticipation = participations.get(0);
        if (lastParticipation.isRewardClaimed()) {
            return;
        }

        Long tournamentGroupId = lastParticipation.getTournamentGroup().getId();
        List<TournamentParticipant> participants = tournamentParticipantRepository
                .findByTournamentGroupIdOrderByScoreDesc(tournamentGroupId);

        if (participants.size() != 5) {
            return;
        }

        int rank = participants.indexOf(lastParticipation) + 1;
        if (rank < 3) {
            throw new UnclaimedRewardException();
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

    public void incrementCountryScore(Country country) {
        countryLeaderboard.compute(country, (key, value) -> value + 1);
    }

    public List<CountryLeaderboardResponse> getCountryLeaderboardResponse(Long tournamentId) {
        List<CountryLeaderboardResponse> leaderboard;

        if (tournamentRepository.findById(tournamentId).isEmpty()) {
            throw new TournamentNotFoundException(tournamentId);
        }

        if (currentTournament.getId().equals(tournamentId)) {
            leaderboard = countryLeaderboard.entrySet().stream()
                    .map(entry -> new CountryLeaderboardResponse(entry.getKey(), entry.getValue()))
                    .sorted((a, b) -> Long.compare(b.score(), a.score()))
                    .toList();
        } else {
            leaderboard = countryLeaderboardRepository.findByTournamentId(tournamentId).stream()
                    .map(countryLeaderboardEntity -> new CountryLeaderboardResponse(
                            countryLeaderboardEntity.getCountry(),
                            countryLeaderboardEntity.getScore())
                    )
                    .sorted((a, b) -> Long.compare(b.score(), a.score()))
                    .toList();
        }

        return leaderboard;
    }

    private void saveCountryLeaderboard(Tournament tournament) {
        countryLeaderboard.forEach((country, score) -> {
            CountryLeaderboard countryLeaderboardEntity = new CountryLeaderboard();
            countryLeaderboardEntity.setCountry(country);
            countryLeaderboardEntity.setScore(score);
            countryLeaderboardEntity.setTournament(tournament);
            countryLeaderboardRepository.save(countryLeaderboardEntity);
        });
    }

    @PreDestroy
    public void onShutdown() {
        saveCountryLeaderboardToFile();
    }

    private void saveCountryLeaderboardToFile() {
        try (FileOutputStream fos = new FileOutputStream(countryLeaderboardFilePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(countryLeaderboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadCountryLeaderboardFromFile() {
        File file = new File(countryLeaderboardFilePath);
        if (!file.exists()) {
            Arrays.stream(Country.values()).forEach(country -> countryLeaderboard.put(country, 0L));
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            ConcurrentHashMap<Country, Long> loadedMap = (ConcurrentHashMap<Country, Long>) ois.readObject();
            countryLeaderboard.clear();
            countryLeaderboard.putAll(loadedMap);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
