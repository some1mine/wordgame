package com.example.demo.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameInfoTest {

    @Test
    void addPlayerMaintainsBothSidesOfRelationship() {
        GameInfo game = GameInfo.builder().build();
        UserInfo user = UserInfo.builder().userId("user").build();

        game.addPlayer(user);

        assertThat(game.getParticipants()).containsExactly(user);
        assertThat(user.getGame()).isSameAs(game);
    }
}
