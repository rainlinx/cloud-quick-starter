package com.xdashen.server.controller;

import com.xdashen.server.model.User;
import com.xdashen.server.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User load(@PathVariable Long id) {
        return userService.load(id);
    }

    @PostMapping
    public User add(@RequestBody User user) {
        return userService.add(user);
    }
}
