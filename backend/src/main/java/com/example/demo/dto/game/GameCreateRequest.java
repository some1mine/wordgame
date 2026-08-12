package com.example.demo.dto.game;

public record GameCreateRequest(
        String initial,
        String name,
        Integer capacity
) {
}
