package com.fitbud.backend.controller;

import com.fitbud.backend.dto.LoginRequest;
import com.fitbud.backend.dto.LoginResponse;
import com.fitbud.backend.dto.RegisterRequest;
import com.fitbud.backend.dto.RegisterResponse;
import com.fitbud.backend.model.UserAccount;
import com.fitbud.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        UserAccount userAccount = authService.register(
                request.email(),
                request.password()
        );

        RegisterResponse response = new RegisterResponse(
                userAccount.getId(),
                userAccount.getEmail(),
                userAccount.getCreatedAt()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(
                request.email(),
                request.password()
        );

        LoginResponse response = new LoginResponse(
                token,
                "Bearer"
        );

        return ResponseEntity.ok(response);
    }
}