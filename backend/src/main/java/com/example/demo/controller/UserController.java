package com.example.demo.controller;

import com.example.demo.domain.entity.UserInfo;
import com.example.demo.dto.user.AuthRequest;
import com.example.demo.dto.user.UserResponse;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<UserResponse> join(@RequestBody AuthRequest request) {
        if (!hasCredentials(request)) {
            return ResponseEntity.badRequest().build();
        }
        if (userService.existsByUserId(request.userId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserInfo user = userService.register(request.userId(), request.password(), request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(HttpServletRequest servletRequest,
                                              @RequestBody AuthRequest request) {
        if (!hasCredentials(request)) {
            return ResponseEntity.badRequest().build();
        }

        UserInfo user = userService.authenticate(request.userId(), request.password());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUserId(), null, Collections.emptyList());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = servletRequest.getSession(true);
        servletRequest.changeSessionId();
        session.setMaxInactiveInterval(60 * 30);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        return ResponseEntity.ok(UserResponse.from(user));
    }

    @GetMapping("/myinfo")
    public ResponseEntity<UserResponse> getMyInfo(Authentication authentication) {
        UserInfo user = userService.findByUserId(authentication.getName());
        return user == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    private boolean hasCredentials(AuthRequest request) {
        return request != null
                && StringUtils.hasText(request.userId())
                && StringUtils.hasText(request.password());
    }
}
