package com.example.demo.dto.game;

import com.fasterxml.jackson.annotation.JsonAlias;

public record SubmitWordRequest(
        Long gameId,
        @JsonAlias("answer") String word
) {
}
