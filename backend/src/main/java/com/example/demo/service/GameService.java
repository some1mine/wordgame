package com.example.demo.service;

import com.example.demo.common.util.WordUtil;
import com.example.demo.domain.entity.GameInfo;
import com.example.demo.domain.entity.UserInfo;
import com.example.demo.domain.enums.RoleInGame;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {

    private final WordUtil wordUtil;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public List<GameInfo> findJoinableGames() {
        return gameRepository.findByIsEndedFalseAndIsStartedFalse();
    }

    public GameInfo findById(long id) {
        return gameRepository.findById(id).orElse(null);
    }

    @Transactional
    public GameInfo makeGame(GameInfo game, UserInfo user) {
        game.setIsStarted(false);
        game.setIsEnded(false);
        GameInfo savedGame = gameRepository.save(game);

        UserInfo managedUser = managedUser(user);
        managedUser.setScore(5);
        managedUser.setRoleInGame(RoleInGame.HOST);
        savedGame.addPlayer(managedUser);
        userRepository.save(managedUser);
        return gameRepository.save(savedGame);
    }

    @Transactional
    public GameInfo joinGame(GameInfo game, UserInfo user) {
        GameInfo managedGame = managedGame(game);
        UserInfo managedUser = managedUser(user);

        managedUser.setScore(5);
        managedUser.setRoleInGame(RoleInGame.PARTICIPANT);
        managedGame.addPlayer(managedUser);

        if (managedGame.getParticipants().size() >= managedGame.getCapacity()) {
            managedGame.setIsStarted(true);
        }

        userRepository.save(managedUser);
        return gameRepository.save(managedGame);
    }

    @Transactional
    public GameInfo exitGame(GameInfo game, UserInfo user) {
        GameInfo managedGame = managedGame(game);
        UserInfo managedUser = managedUser(user);
        boolean hostExited = managedUser.getRoleInGame() == RoleInGame.HOST;

        managedGame.getParticipants().remove(managedUser);
        if (Boolean.TRUE.equals(managedGame.getIsStarted())) {
            managedUser.setLoses(valueOrZero(managedUser.getLoses()) + 1);
        }
        clearGameState(managedUser);
        userRepository.save(managedUser);

        if (managedGame.getParticipants().isEmpty()) {
            managedGame.setIsEnded(true);
        } else if (hostExited) {
            UserInfo nextHost = managedGame.getParticipants().get(0);
            nextHost.setRoleInGame(RoleInGame.HOST);
            userRepository.save(nextHost);
        }

        finishGameIfNeeded(managedGame);
        return gameRepository.save(managedGame);
    }

    @Transactional
    public SubmitResult submit(GameInfo game, UserInfo user, String word) {
        GameInfo managedGame = managedGame(game);
        UserInfo managedUser = managedUser(user);

        boolean correct = wordUtil.getInitials(word).equals(managedGame.getInitial())
                && wordUtil.exists(word);
        int nextScore = valueOrZero(managedUser.getScore()) + (correct ? 1 : -1);
        managedUser.setScore(nextScore);

        boolean eliminated = nextScore <= 0;
        if (eliminated) {
            managedUser.setLoses(valueOrZero(managedUser.getLoses()) + 1);
            managedGame.getParticipants().remove(managedUser);
            clearGameState(managedUser);
        }

        userRepository.save(managedUser);
        finishGameIfNeeded(managedGame);
        GameInfo savedGame = gameRepository.save(managedGame);
        return new SubmitResult(managedUser, correct, eliminated, savedGame);
    }

    @Transactional
    public GameInfo endIfNeeded(GameInfo game) {
        GameInfo managedGame = managedGame(game);
        finishGameIfNeeded(managedGame);
        return gameRepository.save(managedGame);
    }

    private void finishGameIfNeeded(GameInfo game) {
        if (!Boolean.TRUE.equals(game.getIsStarted()) || Boolean.TRUE.equals(game.getIsEnded())) {
            return;
        }
        if (game.getParticipants().size() > 1) {
            return;
        }

        game.setIsEnded(true);
        if (game.getParticipants().size() == 1) {
            UserInfo winner = game.getParticipants().get(0);
            winner.setWins(valueOrZero(winner.getWins()) + 1);
            clearGameState(winner);
            game.getParticipants().remove(winner);
            userRepository.save(winner);
        }
    }

    private UserInfo managedUser(UserInfo user) {
        return userRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));
    }

    private GameInfo managedGame(GameInfo game) {
        return gameRepository.findById(game.getGameKey())
                .orElseThrow(() -> new IllegalArgumentException("Game does not exist"));
    }

    private void clearGameState(UserInfo user) {
        user.setGame(null);
        user.setScore(null);
        user.setRoleInGame(RoleInGame.NONE);
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    public record SubmitResult(
            UserInfo user,
            boolean correct,
            boolean eliminated,
            GameInfo game
    ) {
    }
}
