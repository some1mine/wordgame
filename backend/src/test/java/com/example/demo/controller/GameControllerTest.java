package com.example.demo.controller;

import com.example.demo.domain.entity.GameInfo;
import com.example.demo.domain.entity.UserInfo;
import com.example.demo.domain.enums.RoleInGame;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GameControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @Order(0)
    void join() throws Exception {
        String content = objectMapper.writeValueAsString(UserInfo.builder().userId("controller").password("password").roleInGame(RoleInGame.NONE).build());
        String content2 = objectMapper.writeValueAsString(UserInfo.builder().userId("userid").password("password").roleInGame(RoleInGame.NONE).build());
        mockMvc.perform(post("/user/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(post("/user/join")
                        .content(content2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(1)
    void getAllGamesTest() throws Exception {
        mockMvc.perform(get("/game/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(2)
    void getGameTest() throws Exception {
        mockMvc.perform(get("/game/get")
                        .param("gameid", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @Order(3)
    void makeGameTest() throws Exception {
        String content = objectMapper.writeValueAsString(GameInfo.builder().initial("ㄱㄴ").name("ㄱㄴ").capacity(5).isEnded(false).build());

        mockMvc.perform(post("/game/make-game")
                        .header("userid", "controller")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(4)
    void joinGameTest() throws Exception {
        mockMvc.perform(post("/game/join-game")
                        .header("userid", "userid")
                        .param("gameid", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(5)
    void submitTest() throws Exception {
        mockMvc.perform(post("/game/exit-game")
                        .header("userid", "controller")
                        .param("word", "그네")
                        .param("gameid", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(6)
    void exitGameTest() throws Exception {
        mockMvc.perform(post("/game/exit-game")
                        .header("userid", "userid")
                        .param("gameid", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(7)
    void endIfNeedTest() throws Exception {
        mockMvc.perform(post("/game/end-if-need")
                        .param("gameid", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

}