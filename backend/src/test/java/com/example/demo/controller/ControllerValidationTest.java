package com.example.demo.controller;

import com.example.demo.domain.entity.GameInfo;
import com.example.demo.domain.entity.UserInfo;
import com.example.demo.dto.game.GameCreateRequest;
import com.example.demo.dto.game.GameIdRequest;
import com.example.demo.dto.game.SubmitWordRequest;
import com.example.demo.dto.user.AuthRequest;
import com.example.demo.service.GameService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerValidationTest {

    @Mock UserService userService;
    @Mock GameService gameService;
    @Mock Authentication authentication;

    private UserController userController;
    private GameController gameController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
        gameController = new GameController(gameService, userService);
        lenient().when(authentication.getName()).thenReturn("user");
    }

    @Test
    void joinRejectsDuplicateUserId() {
        AuthRequest request = new AuthRequest("duplicate", "password", null);
        when(userService.existsByUserId("duplicate")).thenReturn(true);

        assertThat(userController.join(request).getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(userService, never()).register(anyString(), anyString(), any());
    }

    @Test
    void makeGameRejectsUnknownSessionUser() {
        when(userService.findByUserId("user")).thenReturn(null);

        assertThat(gameController.makeGame(authentication, new GameCreateRequest("ㄱㅂ", "room", 2))
                .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(gameService, never()).makeGame(any(), any());
    }

    @Test
    void makeGameRejectsInvalidCapacity() {
        when(userService.findByUserId("user")).thenReturn(UserInfo.builder().userId("user").build());

        assertThat(gameController.makeGame(authentication, new GameCreateRequest("ㄱㅂ", "room", 1))
                .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void joinGameRejectsFullGame() {
        UserInfo joiningUser = UserInfo.builder().userId("user").build();
        GameInfo game = GameInfo.builder().capacity(1).isStarted(false).isEnded(false)
                .participants(new ArrayList<>()).build();
        game.getParticipants().add(UserInfo.builder().userId("existing").build());
        when(userService.findByUserId("user")).thenReturn(joiningUser);
        when(gameService.findById(1L)).thenReturn(game);

        assertThat(gameController.joinGame(authentication, new GameIdRequest(1L)).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        verify(gameService, never()).joinGame(any(), any());
    }

    @Test
    void submitRejectsRoomThatHasNotStarted() {
        GameInfo game = GameInfo.builder().gameKey(1L).isStarted(false).isEnded(false)
                .participants(new ArrayList<>()).build();
        UserInfo user = UserInfo.builder().userId("user").game(game).build();
        game.getParticipants().add(user);
        when(userService.findByUserId("user")).thenReturn(user);
        when(gameService.findById(1L)).thenReturn(game);

        assertThat(gameController.submitWord(authentication, new SubmitWordRequest(1L, "가방"))
                .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(gameService, never()).submit(any(), any(), anyString());
    }

    @Test
    void endCheckReturnsNotFoundForUnknownGame() {
        when(gameService.findById(99L)).thenReturn(null);

        assertThat(gameController.endIfNeed(new GameIdRequest(99L)).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
