package com.example.demo.dto.user;

import com.example.demo.domain.entity.UserInfo;

import java.time.LocalDateTime;

public record UserResponse(
        Long userKey,
        String userId,
        String name,
        Integer wins,
        Integer loses,
        LocalDateTime lastLoginDate
) {
    public static UserResponse from(UserInfo user) {
        return new UserResponse(
                user.getUserKey(),
                user.getUserId(),
                user.getName(),
                user.getWins(),
                user.getLoses(),
                user.getLastLoginDate()
        );
    }
}
