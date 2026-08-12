package com.example.demo.service;

import com.example.demo.common.util.WordUtil;
import com.example.demo.domain.entity.GameInfo;
import com.example.demo.domain.entity.UserInfo;
import com.example.demo.domain.enums.RoleInGame;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock WordUtil wordUtil;
    @Mock GameRepository gameRepository;
    @Mock UserRepository userRepository;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(wordUtil, gameRepository, userRepository);
        lenient().when(gameRepository.save(any(GameInfo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(userRepository.save(any(UserInfo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void makeGameAssignsHostButDoesNotStartSinglePlayerRoom() {
        GameInfo game = game(2, false);
        game.setGameKey(null);
        UserInfo host = user("host", 0, RoleInGame.NONE);
        when(userRepository.findByUserId("host")).thenReturn(Optional.of(host));

        GameInfo result = gameService.makeGame(game, host);

        assertThat(result.getParticipants()).containsExactly(host);
        assertThat(result.getIsStarted()).isFalse();
        assertThat(host.getScore()).isEqualTo(5);
        assertThat(host.getRoleInGame()).isEqualTo(RoleInGame.HOST);
    }

    @Test
    void joinGameStartsRoomWhenCapacityIsReached() {
        GameInfo game = game(2, false);
        UserInfo host = user("host", 5, RoleInGame.HOST);
        UserInfo guest = user("guest", 0, RoleInGame.NONE);
        game.addPlayer(host);
        stubManaged(game, guest);

        GameInfo result = gameService.joinGame(game, guest);

        assertThat(result.getParticipants()).containsExactly(host, guest);
        assertThat(result.getIsStarted()).isTrue();
        assertThat(guest.getScore()).isEqualTo(5);
        assertThat(guest.getRoleInGame()).isEqualTo(RoleInGame.PARTICIPANT);
    }

    @Test
    void validAnswerKeepsPlayerInGameAndAddsScore() {
        GameInfo game = game(2, true);
        UserInfo host = user("host", 5, RoleInGame.HOST);
        UserInfo guest = user("guest", 2, RoleInGame.PARTICIPANT);
        game.addPlayer(host);
        game.addPlayer(guest);
        stubManaged(game, guest);
        when(wordUtil.getInitials("가방")).thenReturn("ㄱㅂ");
        when(wordUtil.exists("가방")).thenReturn(true);

        GameService.SubmitResult result = gameService.submit(game, guest, "가방");

        assertThat(result.correct()).isTrue();
        assertThat(result.eliminated()).isFalse();
        assertThat(guest.getScore()).isEqualTo(3);
        assertThat(game.getParticipants()).contains(guest);
    }

    @Test
    void lastWrongAnswerEliminatesPlayerAndFinishesGame() {
        GameInfo game = game(2, true);
        UserInfo host = user("host", 5, RoleInGame.HOST);
        UserInfo guest = user("guest", 1, RoleInGame.PARTICIPANT);
        game.addPlayer(host);
        game.addPlayer(guest);
        stubManaged(game, guest);
        when(wordUtil.getInitials("나비")).thenReturn("ㄴㅂ");

        GameService.SubmitResult result = gameService.submit(game, guest, "나비");

        assertThat(result.eliminated()).isTrue();
        assertThat(guest.getLoses()).isEqualTo(1);
        assertThat(host.getWins()).isEqualTo(1);
        assertThat(game.getIsEnded()).isTrue();
        assertThat(game.getParticipants()).isEmpty();
        assertThat(host.getGame()).isNull();
        assertThat(guest.getGame()).isNull();
    }

    @Test
    void endCheckDoesNotFinishRoomBeforeItStarts() {
        GameInfo game = game(2, false);
        UserInfo host = user("host", 5, RoleInGame.HOST);
        game.addPlayer(host);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        GameInfo result = gameService.endIfNeeded(game);

        assertThat(result.getIsEnded()).isFalse();
        assertThat(host.getWins()).isZero();
    }

    private void stubManaged(GameInfo game, UserInfo user) {
        when(gameRepository.findById(game.getGameKey())).thenReturn(Optional.of(game));
        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
    }

    private GameInfo game(int capacity, boolean started) {
        return GameInfo.builder()
                .gameKey(1L)
                .initial("ㄱㅂ")
                .name("game")
                .capacity(capacity)
                .isStarted(started)
                .isEnded(false)
                .participants(new ArrayList<>())
                .build();
    }

    private UserInfo user(String userId, int score, RoleInGame role) {
        return UserInfo.builder()
                .userId(userId)
                .score(score)
                .wins(0)
                .loses(0)
                .roleInGame(role)
                .build();
    }
}
