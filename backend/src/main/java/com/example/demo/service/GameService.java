package com.example.demo.service;

import com.example.demo.common.util.WordUtil;
import com.example.demo.domain.entity.GameInfo;
import com.example.demo.domain.entity.UserInfo;
import com.example.demo.domain.enums.RoleInGame;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final WordUtil wordUtil;
    private final GameRepository gameRepository;

    public List<GameInfo> findAll() {
        return gameRepository.findAll();
    }

    public GameInfo findById(long id) {
        return gameRepository.findById(id).orElse(null);
    }

    @Transactional
    public GameInfo save(GameInfo game) {
        return gameRepository.save(game);
    }

    public GameInfo makeGame(GameInfo game, UserInfo user) {
        user.setScore(5);
        user.setRoleInGame(RoleInGame.HOST);
        game.addPlayer(user);

        return save(game);
    }

    public GameInfo joinGame(GameInfo game, UserInfo user) {
        user.setScore(5);
        user.setRoleInGame(RoleInGame.PARTICIPANT);
        user.setGame(game);

        game.addPlayer(user);

        return save(game);
    }

    public UserInfo exitGame(GameInfo game, UserInfo user) {
        game.getParticipants().remove(user);
        user.setGame(null); user.setScore(null); user.setRoleInGame(RoleInGame.NONE);

        save(game);

        return user;
    }

    public UserInfo submit(GameInfo game, UserInfo user, String word) {
        int score = wordUtil.getInitials(word).equals(game.getInitial()) && wordUtil.exists(word) ? 1 : -1;
        user.setScore(user.getScore() + score);

        if (user.getScore() == 0) {
            user.setLoses(user.getLoses() + 1);
            user.setGame(null); user.setScore(null); user.setRoleInGame(RoleInGame.NONE);
        }

        game.getParticipants().remove(user);
        save(game);

        return user;
    }

}
