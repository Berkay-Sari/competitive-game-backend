package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public User createUser() {
        return userService.createUser();
    }

    @PutMapping("/update-level/{userId}")
    public User updateLevel(@PathVariable Long userId) {
        return userService.updateLevel(userId);
    }
}
