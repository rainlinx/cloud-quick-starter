package com.xdashen.server.controller;

import com.xdashen.server.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/login")
public class LoginController {
    @GetMapping
    public User login(@RequestParam String userName, @RequestParam String password) {
        if (Objects.equals(userName, password)) {
            return User.builder().userName(userName).build();
        } else {
            throw new RuntimeException("用户名或密码错误");
        }
    }
}
