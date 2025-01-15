package com.example.coursescheduler.service;

import com.example.coursescheduler.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User registerUser(User user) {
        return userRepository.save(user);
    }
}
