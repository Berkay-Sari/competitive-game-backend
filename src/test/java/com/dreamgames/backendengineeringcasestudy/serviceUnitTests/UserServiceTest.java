package com.dreamgames.backendengineeringcasestudy.serviceUnitTests;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.enums.Country;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.repo.UserRepository;
import com.dreamgames.backendengineeringcasestudy.service.TournamentParticipantService;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TournamentParticipantService tournamentParticipantService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void should_ReturnUserWithRandomCountry_When_CreateUser() {
        User user = new User();
        user.setCountry(Country.values()[0]);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser();

        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(user, createdUser);
    }

    @Test
    void should_IncreaseUserLevelAndCoins_When_UpdateLevel() {
        User user = new User();
        user.setId(1L);
        user.setLevel(1);
        user.setCoins(5000);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tournamentParticipantService.hasCompetitionBeginForUser(any(User.class))).thenReturn(false);

        User updatedUser = userService.updateLevel(1L);

        assertEquals(2, updatedUser.getLevel());
        assertEquals(5025, updatedUser.getCoins());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void should_ThrowUserNotFoundException_When_UpdateLevelWithInvalidUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateLevel(1L));
    }

    @Test
    void should_IncreaseUserScore_When_CompetitionBegun() {
        User user = new User();
        user.setId(1L);
        user.setLevel(1);
        user.setCoins(100);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tournamentParticipantService.hasCompetitionBeginForUser(any(User.class))).thenReturn(true);

        userService.updateLevel(1L);

        verify(tournamentParticipantService, times(1)).increaseUserScore(user);
    }
}
