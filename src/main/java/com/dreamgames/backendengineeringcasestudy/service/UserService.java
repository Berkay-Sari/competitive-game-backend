package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.Country;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final Country[] COUNTRIES = Country.values();
    private static final Random RANDOM = new Random();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser() {
        User user = new User();
        user.setCountry(COUNTRIES[RANDOM.nextInt(COUNTRIES.length)]);
        return userRepository.save(user);
    }

    public User updateLevel(Long userId) {
        synchronized (userId) {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            user.setLevel(user.getLevel() + 1);
            user.setCoins(user.getCoins() + 25);
            return userRepository.save(user);
        }
    }
}

