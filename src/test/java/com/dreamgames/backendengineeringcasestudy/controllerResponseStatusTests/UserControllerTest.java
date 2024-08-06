package com.dreamgames.backendengineeringcasestudy.controllerResponseStatusTests;

import com.dreamgames.backendengineeringcasestudy.controller.UserController;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.exception.CustomExceptionHandler;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(customExceptionHandler)
                .build();
    }

    @Test
    void should_ReturnCreatedUser_When_CreateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.createUser()).thenReturn(user);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void should_ReturnUpdatedUser_When_UpdateLevel() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.updateLevel(anyLong())).thenReturn(user);

        mockMvc.perform(put("/users/update-level/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void should_ReturnNotFound_When_UpdateLevelWithInvalidUserId() throws Exception {
        when(userService.updateLevel(anyLong())).thenThrow(new UserNotFoundException(1L));

        mockMvc.perform(put("/users/update-level/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
