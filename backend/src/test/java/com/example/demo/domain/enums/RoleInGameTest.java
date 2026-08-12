package com.example.demo.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleInGameTest {

    @Test
    void exposesCodeAndStringValue() {
        assertThat(RoleInGame.HOST.getCode()).isZero();
        assertThat(RoleInGame.PARTICIPANT.toString()).isEqualTo("1");
        assertThat(RoleInGame.NONE.toString()).isEqualTo("-1");
    }
}
