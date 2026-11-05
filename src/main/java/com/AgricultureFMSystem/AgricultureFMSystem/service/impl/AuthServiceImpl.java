package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.LoginRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.RegisterRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.AuthResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.User;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.UserRole;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.BadRequestException;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.DuplicateResourceException;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.UserRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        String identifier = requestDTO.getUsernameOrEmail() != null ? requestDTO.getUsernameOrEmail().trim() : "";
        String rawPassword = requestDTO.getPassword() != null ? requestDTO.getPassword().trim() : "";

        log.info("Processing login request for: '{}'", identifier);

        User user = userRepository.findByUsernameOrEmailIgnoreCase(identifier)
                .orElseThrow(() -> new BadRequestException("Invalid username/email or password"));

        if (user.getActive() != null && !user.getActive()) {
            throw new BadRequestException("Account is currently disabled. Please contact the farm administrator.");
        }

        String inputHashed = hashPassword(rawPassword);
        boolean passwordMatches = user.getPassword().equalsIgnoreCase(inputHashed)
                || user.getPassword().equals(rawPassword);

        if (!passwordMatches) {
            log.warn("Password mismatch for user '{}'", identifier);
            throw new BadRequestException("Invalid username/email or password");
        }

        String token = "afms_jwt_" + UUID.randomUUID().toString().replace("-", "") + "_"
                + Base64.getEncoder().encodeToString(user.getUsername().getBytes(StandardCharsets.UTF_8));

        log.info("User '{}' (role: {}) logged in successfully", user.getUsername(), user.getRole());

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO requestDTO) {
        String username = requestDTO.getUsername().trim();
        String email = requestDTO.getEmail().trim().toLowerCase();

        log.info("Registering user: '{}', email: '{}'", username, email);

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException("Username '" + username + "' is already taken.");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email '" + email + "' is already in use.");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(hashPassword(requestDTO.getPassword().trim()))
                .fullName(requestDTO.getFullName().trim())
                .role(requestDTO.getRole() != null ? requestDTO.getRole() : UserRole.FARMER)
                .active(true)
                .build();

        User saved = userRepository.save(user);
        String token = "afms_jwt_" + UUID.randomUUID().toString().replace("-", "");

        log.info("User registered successfully with ID: {}", saved.getId());

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .role(saved.getRole())
                .message("User registered successfully")
                .build();
    }

    @Override
    public AuthResponseDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return AuthResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .message("User profile retrieved successfully")
                .build();
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }
}
