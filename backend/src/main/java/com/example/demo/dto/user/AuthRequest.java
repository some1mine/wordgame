package com.example.demo.dto.user;

import com.fasterxml.jackson.annotation.JsonAlias;

public record AuthRequest(
        @JsonAlias("username") String userId,
        String password,
        String name
) {
}
