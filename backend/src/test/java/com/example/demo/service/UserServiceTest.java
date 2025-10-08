package com.example.demo.service;

import com.example.demo.domain.enums.RoleInGame;
import com.example.demo.domain.entity.GameInfo;
import com.example.demo.domain.entity.UserInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {
    @Autowired UserService userService;
    @Autowired GameService gameService;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    @Order(1)
    void testJoin(){
        UserInfo user = UserInfo.builder().userId("service").password("password").name("유저").build();
        UserInfo savedUser = userService.save(user);

        Assertions.assertThat(user.getUserId() + user.getPassword()).isEqualTo(savedUser.getUserId() + savedUser.getPassword());
    }

    @Test
    @Order(2)
    void testLogin() {
        UserInfo userInfo = UserInfo.builder().userId("service").password("password").roleInGame(RoleInGame.NONE).build();
        UserInfo foundUser = userService.findByUserId(userInfo.passwordEncodedUser(passwordEncoder).getUserId());

        Assertions.assertThat(foundUser.getName()).isEqualTo("유저");
    }

    @Test
    @Order(3)
    void testMyInfo() {
        String userId = "service";
        UserInfo foundUser = userService.findByUserId(userId);

        Assertions.assertThat(foundUser.getName()).isEqualTo("유저");
    }

    @Test
    @Order(4)
    void testMakeGame() {
        UserInfo user = userService.findByUserId("service");
        user.setScore(5);
        user.setRoleInGame(RoleInGame.HOST);

        GameInfo game = GameInfo.builder().initial("ㄷㅈ").name("동주").capacity(10).isEnded(false).build();
        user.setGame(game);
        game.getParticipants().add(user);

        GameInfo savedGame = gameService.save(game);

        Assertions.assertThat(savedGame.getParticipants().get(0).getGame()).isEqualTo(game);
    }

    @Test
    @Order(5)
    @Transactional
    void testJoinGame() {
        UserInfo user = userService.findByUserId("service");
        GameInfo game = gameService.findAll().get(0);

        user.setRoleInGame(RoleInGame.PARTICIPANT);
        user.setGame(game);

        Assertions.assertThat(gameService.joinGame(game, user).getParticipants().stream().anyMatch(p -> p.equals(user))).isEqualTo(true);
    }

}