package com.example.demo.controller;

import com.example.demo.domain.entity.GameInfo;
import com.example.demo.domain.entity.UserInfo;
import com.example.demo.dto.game.*;
import com.example.demo.service.GameService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private static final int MAX_CAPACITY = 20;

    private final GameService gameService;
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<GameResponse>> getAll() {
        return ResponseEntity.ok(gameService.findJoinableGames().stream()
                .map(GameResponse::from)
                .toList());
    }

    @GetMapping("/get")
    public ResponseEntity<GameResponse> findById(@RequestParam long gameId) {
        GameInfo game = gameService.findById(gameId);
        return game == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(GameResponse.from(game));
    }

    @PostMapping("/make-game")
    public ResponseEntity<GameResponse> makeGame(Authentication authentication,
                                                 @RequestBody GameCreateRequest request) {
        UserInfo user = currentUser(authentication);
        if (user == null || user.getGame() != null || !validGameRequest(request)) {
            return ResponseEntity.badRequest().build();
        }

        GameInfo game = GameInfo.builder()
                .initial(request.initial().trim())
                .name(request.name().trim())
                .capacity(request.capacity())
                .isStarted(false)
                .isEnded(false)
                .build();
        return ResponseEntity.ok(GameResponse.from(gameService.makeGame(game, user)));
    }

    @PostMapping("/join-game")
    public ResponseEntity<GameResponse> joinGame(Authentication authentication,
                                                 @RequestBody GameIdRequest request) {
        UserInfo user = currentUser(authentication);
        if (user == null || user.getGame() != null || request == null || request.gameId() == null) {
            return ResponseEntity.badRequest().build();
        }

        GameInfo game = gameService.findById(request.gameId());
        if (!canJoin(game, user.getUserId())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(GameResponse.from(gameService.joinGame(game, user)));
    }

    @PostMapping("/submit")
    public ResponseEntity<SubmissionResponse> submitWord(Authentication authentication,
                                                         @RequestBody SubmitWordRequest request) {
        UserInfo user = currentUser(authentication);
        if (user == null || request == null || request.gameId() == null
                || !StringUtils.hasText(request.word())) {
            return ResponseEntity.badRequest().build();
        }

        GameInfo game = gameService.findById(request.gameId());
        if (!canPlay(game, user)) {
            return ResponseEntity.badRequest().build();
        }

        GameService.SubmitResult result = gameService.submit(game, user, request.word().trim());
        return ResponseEntity.ok(new SubmissionResponse(
                result.user().getUserId(),
                result.user().getScore(),
                result.correct(),
                result.eliminated(),
                GameResponse.from(result.game())
        ));
    }

    @PostMapping("/exit-game")
    public ResponseEntity<GameResponse> exitGame(Authentication authentication,
                                                 @RequestBody GameIdRequest request) {
        UserInfo user = currentUser(authentication);
        if (user == null || request == null || request.gameId() == null) {
            return ResponseEntity.badRequest().build();
        }

        GameInfo game = gameService.findById(request.gameId());
        if (!belongsToGame(game, user)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(GameResponse.from(gameService.exitGame(game, user)));
    }

    @PostMapping("/end-if-need")
    public ResponseEntity<GameResponse> endIfNeed(@RequestBody GameIdRequest request) {
        if (request == null || request.gameId() == null) {
            return ResponseEntity.badRequest().build();
        }

        GameInfo game = gameService.findById(request.gameId());
        if (game == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(GameResponse.from(gameService.endIfNeeded(game)));
    }

    private UserInfo currentUser(Authentication authentication) {
        return authentication == null ? null : userService.findByUserId(authentication.getName());
    }

    private boolean validGameRequest(GameCreateRequest request) {
        return request != null
                && StringUtils.hasText(request.initial())
                && StringUtils.hasText(request.name())
                && request.capacity() != null
                && request.capacity() >= 2
                && request.capacity() <= MAX_CAPACITY;
    }

    private boolean canJoin(GameInfo game, String userId) {
        return game != null
                && !Boolean.TRUE.equals(game.getIsStarted())
                && !Boolean.TRUE.equals(game.getIsEnded())
                && game.getCapacity() != null
                && game.getParticipants().size() < game.getCapacity()
                && game.getParticipants().stream()
                .noneMatch(player -> player.getUserId().equals(userId));
    }

    private boolean canPlay(GameInfo game, UserInfo user) {
        return belongsToGame(game, user)
                && Boolean.TRUE.equals(game.getIsStarted())
                && !Boolean.TRUE.equals(game.getIsEnded());
    }

    private boolean belongsToGame(GameInfo game, UserInfo user) {
        return game != null
                && user.getGame() != null
                && user.getGame().getGameKey().equals(game.getGameKey())
                && game.getParticipants().stream()
                .anyMatch(player -> player.getUserId().equals(user.getUserId()));
    }
}
