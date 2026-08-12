package com.example.demo.service;

import com.example.demo.domain.entity.GameInfo;
import com.example.demo.domain.entity.UserInfo;
import com.example.demo.domain.enums.RoleInGame;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registerEncodesPasswordAndInitializesUser() {
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(any(UserInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserInfo result = userService.register("user", "plain", "name");

        assertThat(result.getPassword()).isEqualTo("encoded");
        assertThat(result.getWins()).isZero();
        assertThat(result.getLoses()).isZero();
        assertThat(result.getRoleInGame()).isEqualTo(RoleInGame.NONE);
    }

    @Test
    void authenticateAcceptsCorrectRawPasswordWithoutReencoding() {
        UserInfo user = UserInfo.builder().userId("user").password("encoded").build();
        when(userRepository.findByUserId("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain", "encoded")).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        UserInfo result = userService.authenticate("user", "plain");

        assertThat(result).isSameAs(user);
        assertThat(result.getLastLoginDate()).isNotNull();
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void authenticateRejectsWrongPassword() {
        UserInfo user = UserInfo.builder().userId("user").password("encoded").build();
        when(userRepository.findByUserId("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThat(userService.authenticate("user", "wrong")).isNull();
        verify(userRepository, never()).save(any());
    }

    @Test
    void findByUserIdReturnsNullWhenMissing() {
        when(userRepository.findByUserId("missing")).thenReturn(Optional.empty());

        assertThat(userService.findByUserId("missing")).isNull();
    }

    @Test
    void plusWinCountDoesNotReencodePassword() {
        UserInfo user = UserInfo.builder()
                .userId("winner").password("encoded").wins(2).score(4)
                .roleInGame(RoleInGame.HOST).game(GameInfo.builder().build()).build();
        when(userRepository.save(user)).thenReturn(user);

        UserInfo result = userService.plusWinCount(user);

        assertThat(result.getWins()).isEqualTo(3);
        assertThat(result.getGame()).isNull();
        assertThat(result.getScore()).isNull();
        assertThat(result.getRoleInGame()).isEqualTo(RoleInGame.NONE);
        verifyNoInteractions(passwordEncoder);
    }
}
