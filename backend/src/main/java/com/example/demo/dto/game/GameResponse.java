package com.example.demo.dto.game;

import com.example.demo.domain.entity.GameInfo;

import java.util.List;

public record GameResponse(
        Long gameId,
        String initial,
        String name,
        Integer capacity,
        Boolean isStarted,
        Boolean isEnded,
        List<PlayerResponse> participants
) {
    public static GameResponse from(GameInfo game) {
        return new GameResponse(
                game.getGameKey(),
                game.getInitial(),
                game.getName(),
                game.getCapacity(),
                game.getIsStarted(),
                game.getIsEnded(),
                game.getParticipants().stream().map(PlayerResponse::from).toList()
        );
    }
}
