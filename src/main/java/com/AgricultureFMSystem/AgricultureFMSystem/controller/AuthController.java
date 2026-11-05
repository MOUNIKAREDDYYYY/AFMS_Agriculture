package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.LoginRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.RegisterRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.AuthResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "0. Authentication & Security", description = "Endpoints for User Login, Registration, and Identity Management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user with username or email and password, returning a Bearer token.")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        log.info("REST request to login: {}", requestDTO.getUsernameOrEmail());
        AuthResponseDTO response = authService.login(requestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Registers a new user (Farmer, Agronomist, Supervisor, Admin).")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        log.info("REST request to register user: {}", requestDTO.getUsername());
        AuthResponseDTO response = authService.register(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User Profile", description = "Fetches user details by username.")
    public ResponseEntity<AuthResponseDTO> getCurrentUser(@RequestParam String username) {
        log.debug("REST request for profile of user: {}", username);
        return ResponseEntity.ok(authService.getCurrentUser(username));
    }
}
