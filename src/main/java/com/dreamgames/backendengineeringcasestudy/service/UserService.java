package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.enums.Country;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TournamentParticipantService tournamentParticipantService;
    private static final Country[] COUNTRIES = Country.values();
    private static final Random RANDOM = new Random();

    public UserService(UserRepository userRepository, TournamentParticipantService tournamentParticipantService) {
        this.userRepository = userRepository;
        this.tournamentParticipantService = tournamentParticipantService;
    }

    public User createUser() {
        User user = new User();
        user.setCountry(COUNTRIES[RANDOM.nextInt(COUNTRIES.length)]);
        return userRepository.save(user);
    }

    public User updateLevel(Long userId) {
        User user;
        synchronized (userId) {
            user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
            user.setLevel(user.getLevel() + 1);
            user.setCoins(user.getCoins() + 25);
            user = userRepository.save(user);
        }

        if (tournamentParticipantService.hasCompetitionBeginForUser(user)) {
            tournamentParticipantService.increaseUserScore(user);
        }

        return user;
    }
}

