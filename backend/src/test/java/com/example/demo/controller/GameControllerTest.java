package com.example.demo.controller;

import com.example.demo.repository.GameRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired GameRepository gameRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
        gameRepository.deleteAll();
    }

    @Test
    void registrationLoginCreateJoinSubmitAndEndFlow() throws Exception {
        MockHttpSession hostSession = registerAndLogin("host");
        MockHttpSession guestSession = registerAndLogin("guest");

        MvcResult makeResult = mockMvc.perform(post("/game/make-game")
                        .session(hostSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "initial", "ㄱㅂ",
                                "name", "test-room",
                                "capacity", 2
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isStarted").value(false))
                .andExpect(jsonPath("$.participants.length()").value(1))
                .andReturn();
        long gameId = objectMapper.readTree(makeResult.getResponse().getContentAsString())
                .get("gameId").asLong();

        mockMvc.perform(post("/game/join-game")
                        .session(guestSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("gameId", gameId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isStarted").value(true))
                .andExpect(jsonPath("$.participants.length()").value(2));

        for (int attempt = 1; attempt <= 4; attempt++) {
            mockMvc.perform(post("/game/submit")
                            .session(guestSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "gameId", gameId,
                                    "word", "나비"
                            ))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.eliminated").value(false))
                    .andExpect(jsonPath("$.game.isEnded").value(false));
        }

        mockMvc.perform(post("/game/submit")
                        .session(guestSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "gameId", gameId,
                                "answer", "나비"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eliminated").value(true))
                .andExpect(jsonPath("$.game.isEnded").value(true));

        mockMvc.perform(post("/game/end-if-need")
                        .session(hostSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("gameId", gameId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isEnded").value(true));

        mockMvc.perform(get("/user/myinfo").session(hostSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wins").value(1));
        mockMvc.perform(get("/user/myinfo").session(guestSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loses").value(1));
    }

    @Test
    void gameEndpointsRequireAuthenticatedSession() throws Exception {
        mockMvc.perform(get("/game/all"))
                .andExpect(status().isUnauthorized());
    }

    private MockHttpSession registerAndLogin(String userId) throws Exception {
        String credentials = objectMapper.writeValueAsString(Map.of(
                "userId", userId,
                "password", "password"
        ));
        mockMvc.perform(post("/user/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials))
                .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) loginResult.getRequest().getSession(false);
    }
}
