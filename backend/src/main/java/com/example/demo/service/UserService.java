package com.example.demo.service;

import com.example.demo.domain.entity.UserInfo;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserInfo save(UserInfo user) {
        return userRepository.save(user.passwordEncodedUser(passwordEncoder));
    }

    public UserInfo findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }

    public UserInfo plusWinCount(UserInfo user) {
        user.setWins(user.getWins() + 1);
        user.setGame(null); user.setScore(null); user.setRoleInGame(null);
        return save(user);
    }


}
