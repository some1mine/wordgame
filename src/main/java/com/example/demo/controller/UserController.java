package com.example.demo.controller;

import com.example.demo.domain.entity.UserInfo;
import com.example.demo.service.GameService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final GameService gameService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/join")
    public ResponseEntity<UserInfo> join(@RequestBody UserInfo user) {
        if (userService.findByUserId(user.getUserId()) != null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(userService.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserInfo> login(HttpServletRequest request, @RequestBody UserInfo userInfo) {
        UserInfo user = userService.findByUserId(userInfo.passwordEncodedUser(passwordEncoder).getUserId());
        if (user == null || passwordEncoder.matches(userInfo.getPassword(), user.getPassword())) return ResponseEntity.badRequest().build();

        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(60 * 30);
        session.setAttribute("userId", user.getUserId());

        user.setLastLoginDate(LocalDateTime.now());
        user = userService.save(user);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/myinfo")
    public ResponseEntity<UserInfo> getMyInfo(@RequestHeader(name = "userid") String id) {
        return ResponseEntity.ok(userService.findByUserId(id));
    }

}
