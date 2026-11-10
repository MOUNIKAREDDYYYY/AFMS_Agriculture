package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.security.JwtTokenUtil;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.LoginRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.RegisterRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.AuthResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.User;
import com.AgricultureFMSystem.AgricultureFMSystem.enums.UserRole;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.BadRequestException;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.DuplicateResourceException;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.UserRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private static final String PBKDF2_PREFIX = "pbkdf2$";
    private static final int PBKDF2_ITERATIONS = 65_536;
    private static final int PBKDF2_KEY_LENGTH = 256;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        String identifier = requestDTO.getUsernameOrEmail() != null ? requestDTO.getUsernameOrEmail().trim() : "";
        String rawPassword = requestDTO.getPassword() != null ? requestDTO.getPassword().trim() : "";

        log.info("Processing login request for: '{}'", identifier);

        User user = userRepository.findByUsernameOrEmailIgnoreCase(identifier)
                .orElseThrow(() -> new BadRequestException("Invalid username/email or password"));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new BadRequestException("Account is currently disabled. Please contact the farm administrator.");
        }

        if (!verifyPassword(rawPassword, user.getPassword())) {
            log.warn("Password mismatch for user '{}'", identifier);
            throw new BadRequestException("Invalid username/email or password");
        }

        String token = jwtTokenUtil.generateToken(user.getUsername(), user.getRole());

        log.info("User '{}' (role: {}) logged in successfully", user.getUsername(), user.getRole());

        return buildAuthResponse(user, token, "Login successful");
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
        String token = jwtTokenUtil.generateToken(saved.getUsername(), saved.getRole());

        log.info("User registered successfully with ID: {}", saved.getId());

        return buildAuthResponse(saved, token, "User registered successfully");
    }

    @Override
    public AuthResponseDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return buildAuthResponse(user, null, "User profile retrieved successfully");
    }

    private AuthResponseDTO buildAuthResponse(User user, String token, String message) {
        return AuthResponseDTO.builder()
                .token(token)
                .tokenType(token != null ? "Bearer" : null)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .message(message)
                .build();
    }

    public static String hashPassword(String password) {
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        byte[] derived = pbkdf2(password.toCharArray(), salt);
        return PBKDF2_PREFIX
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(derived);
    }

    private static boolean verifyPassword(String rawPassword, String storedHash) {
        if (storedHash == null || storedHash.isBlank()) {
            return false;
        }
        if (storedHash.startsWith(PBKDF2_PREFIX)) {
            String[] parts = storedHash.substring(PBKDF2_PREFIX.length()).split("\\$");
            if (parts.length != 2) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expected = Base64.getDecoder().decode(parts[1]);
            byte[] actual = pbkdf2(rawPassword.toCharArray(), salt);
            return MessageDigest.isEqual(expected, actual);
        }
        // Backward compatibility for legacy SHA-256 hashes seeded earlier in MySQL
        return storedHash.equalsIgnoreCase(legacySha256(rawPassword));
    }

    private static byte[] pbkdf2(char[] password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("PBKDF2 password hashing unavailable", ex);
        }
    }

    private static String legacySha256(String password) {
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
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", ex);
        }
    }
}
