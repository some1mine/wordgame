package com.example.demo.dto.game;

public record SubmissionResponse(
        String userId,
        Integer score,
        boolean correct,
        boolean eliminated,
        GameResponse game
) {
}
