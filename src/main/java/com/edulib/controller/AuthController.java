package com.edulib.controller;

import com.edulib.dto.request.AuthRequest;
import com.edulib.dto.response.ApiResponse;
import com.edulib.dto.response.AuthResponse;
import com.edulib.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/v1/auth/register
     * Register a new user account (role: USER by default).
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse.TokenResponse>> register(
            @Valid @RequestBody AuthRequest.Register request) {
        log.info("Register request for email: {}", request.getEmail());
        AuthResponse.TokenResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    /**
     * POST /api/v1/auth/login
     * Authenticate and receive a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse.TokenResponse>> login(
            @Valid @RequestBody AuthRequest.Login request) {
        log.info("Login request for email: {}", request.getEmail());
        AuthResponse.TokenResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
