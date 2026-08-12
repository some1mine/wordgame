package com.example.demo.service;

import com.example.demo.domain.entity.UserInfo;
import com.example.demo.domain.enums.RoleInGame;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    @Transactional
    public UserInfo register(String userId, String rawPassword, String name) {
        UserInfo user = UserInfo.builder()
                .userId(userId)
                .password(passwordEncoder.encode(rawPassword))
                .name(name)
                .wins(0)
                .loses(0)
                .roleInGame(RoleInGame.NONE)
                .build();
        return userRepository.save(user);
    }

    public UserInfo findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }

    @Transactional
    public UserInfo authenticate(String userId, String rawPassword) {
        UserInfo user = findByUserId(userId);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            return null;
        }

        user.setLastLoginDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public UserInfo plusWinCount(UserInfo user) {
        user.setWins(valueOrZero(user.getWins()) + 1);
        user.setGame(null);
        user.setScore(null);
        user.setRoleInGame(RoleInGame.NONE);
        return userRepository.save(user);
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
