package com.xdashen.server.service;

import com.xdashen.server.model.User;
import com.xdashen.server.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User add(User user) {
        return userRepository.save(user);
    }

    public User load(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户" + id + "不存在"));
    }
}
