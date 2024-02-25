package com.example.demo.controller;

import com.example.demo.common.util.WordUtil;
import com.example.demo.domain.entity.GameInfo;
import com.example.demo.domain.entity.UserInfo;
import com.example.demo.service.GameService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<GameInfo>> getAll() {
        return ResponseEntity.ok(gameService.findAll());
    }

    @GetMapping("/get")
    public ResponseEntity<GameInfo> findById(@RequestParam(name = "gameid") long gameId) {
        return ResponseEntity.ok(gameService.findById(gameId));
    }

    @PostMapping("/makegame")
    public ResponseEntity<GameInfo> makeGame(@RequestHeader(name = "userid") String userId, @RequestBody GameInfo game) {
        UserInfo user = userService.findByUserId(userId);
        if (user == null || user.getGame() != null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(gameService.makeGame(game, user));
    }

    @PostMapping("/joingame")
    public ResponseEntity<GameInfo> joinGame(@RequestHeader(name = "userid") String userId, @RequestParam(name = "gameid") long gameId) {
        UserInfo user = userService.findByUserId(userId);
        if (user == null || user.getGame() != null) return ResponseEntity.badRequest().build();

        GameInfo game = gameService.findById(gameId);
        if (game == null || game.getCapacity() == game.getParticipants().size()) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(gameService.joinGame(game, user));
    }
    @PostMapping("/exitgame")
    public ResponseEntity<UserInfo> exitGame(@RequestHeader(name = "userid") String userId, @RequestParam(name = "gameid") long gameId) {
        UserInfo user = userService.findByUserId(userId);
        if (user == null || user.getGame() == null) return ResponseEntity.badRequest().build();

        GameInfo game = gameService.findById(gameId);
        if (game == null || game.getParticipants().stream().noneMatch(p -> p.getUserId().equals(userId))) return ResponseEntity.badRequest().build();

        UserInfo savedUser = gameService.exitGame(game, user);

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/submit")
    public ResponseEntity<UserInfo> submitWord(@RequestHeader(name = "userid") String userId, @RequestParam String word, @RequestParam(name = "gameid") long gameId) {
        UserInfo user = userService.findByUserId(userId);
        if (user == null || user.getGame() == null) return ResponseEntity.badRequest().build();

        GameInfo game = gameService.findById(gameId);
        if (game == null || game.getParticipants().stream().noneMatch(p -> p.getUserId().equals(userId))) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(gameService.submit(game, user, word));
    }
    @Transactional
    @PostMapping("/endifneed")
    public ResponseEntity<GameInfo> endIfNeed(@RequestParam(name = "gameid") long gameId) {
        GameInfo game = gameService.findById(gameId);

        if (game.getParticipants().stream().filter(p -> p.getScore() != null).count() == 1) {
            game.getParticipants().stream().filter(p -> p.getScore() > 0).forEach(userService::plusWinCount);
            game.setIsEnded(true);
        }

        return ResponseEntity.ok(gameService.save(game));
    }

}
