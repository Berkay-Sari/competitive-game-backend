package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {

    private static final String[] COUNTRIES = {"Turkey", "United States", "United Kingdom", "France", "Germany"};
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser() {
        User user = new User();
        user.setCountry(COUNTRIES[new Random().nextInt(COUNTRIES.length)]);
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

