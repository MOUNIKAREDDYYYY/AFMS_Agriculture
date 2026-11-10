package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.LoginRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.RegisterRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.AuthResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.User;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.UnauthorizedException;
import com.AgricultureFMSystem.AgricultureFMSystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "0. Authentication & Security", description = "Endpoints for User Login, Registration, and Identity Management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user with username or email and password, returning a Bearer JWT.")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        return ResponseEntity.ok(authService.login(requestDTO));
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Registers a new user (Farmer, Agronomist, Supervisor, Admin).")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        return new ResponseEntity<>(authService.register(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User Profile", description = "Returns the profile of the authenticated Bearer token user.")
    public ResponseEntity<AuthResponseDTO> getCurrentUser(HttpServletRequest request) {
        Object principal = request.getAttribute("CURRENT_USER");
        if (!(principal instanceof User currentUser)) {
            throw new UnauthorizedException("Authorization Bearer token is required.");
        }
        return ResponseEntity.ok(authService.getCurrentUser(currentUser.getUsername()));
    }
}
