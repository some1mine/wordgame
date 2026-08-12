package com.example.demo.dto.game;

import com.example.demo.domain.entity.UserInfo;
import com.example.demo.domain.enums.RoleInGame;

public record PlayerResponse(
        String userId,
        String name,
        Integer score,
        RoleInGame role
) {
    public static PlayerResponse from(UserInfo user) {
        return new PlayerResponse(
                user.getUserId(),
                user.getName(),
                user.getScore(),
                user.getRoleInGame()
        );
    }
}
