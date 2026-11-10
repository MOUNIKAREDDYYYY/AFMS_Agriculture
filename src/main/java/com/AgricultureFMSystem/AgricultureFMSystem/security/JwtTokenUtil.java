package com.AgricultureFMSystem.AgricultureFMSystem.security;

import com.AgricultureFMSystem.AgricultureFMSystem.enums.UserRole;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates and verifies HMAC-SHA256 signed JSON Web Tokens (JWT) with expiration.
 */
@Component
public class JwtTokenUtil {

    private static final String DEFAULT_SECRET = "afms-enterprise-agriculture-jwt-secret-key-2026-change-in-prod";
    private static final long DEFAULT_EXPIRATION_MS = 86_400_000L; // 24 hours
    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final Pattern SUB_PATTERN = Pattern.compile("\"sub\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern EXP_PATTERN = Pattern.compile("\"exp\"\\s*:\\s*(\\d+)");

    private final byte[] secretBytes;
    private final long expirationMs;

    public JwtTokenUtil() {
        this(DEFAULT_SECRET, DEFAULT_EXPIRATION_MS);
    }

    @Autowired
    public JwtTokenUtil(
            @Value("${app.security.jwt-secret:" + DEFAULT_SECRET + "}") String secret,
            @Value("${app.security.jwt-expiration-ms:86400000}") long expirationMs) {
        this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, UserRole role) {
        long nowSeconds = Instant.now().getEpochSecond();
        long expSeconds = nowSeconds + Math.max(1L, expirationMs / 1000L);
        String safeUsername = escapeJson(username);
        String payloadJson = "{\"sub\":\"" + safeUsername + "\",\"role\":\"" + role.name()
                + "\",\"iat\":" + nowSeconds + ",\"exp\":" + expSeconds + "}";

        String encodedHeader = base64UrlEncode(HEADER_JSON.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signingInput = encodedHeader + "." + encodedPayload;
        String signature = base64UrlEncode(hmacSha256(signingInput));
        return signingInput + "." + signature;
    }

    public String validateAndExtractUsername(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Bearer token is missing.");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3 || parts[0].isBlank() || parts[1].isBlank() || parts[2].isBlank()) {
            throw new UnauthorizedException("Invalid JWT token format.");
        }

        String signingInput = parts[0] + "." + parts[1];
        byte[] expectedSig = hmacSha256(signingInput);
        byte[] actualSig;
        try {
            actualSig = Base64.getUrlDecoder().decode(parts[2]);
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException("Invalid JWT signature encoding.");
        }

        if (!MessageDigest.isEqual(expectedSig, actualSig)) {
            throw new UnauthorizedException("Invalid JWT signature.");
        }

        String payloadJson;
        try {
            payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException("Invalid JWT payload encoding.");
        }

        Matcher expMatcher = EXP_PATTERN.matcher(payloadJson);
        if (!expMatcher.find()) {
            throw new UnauthorizedException("JWT token is missing expiration.");
        }
        long expSeconds = Long.parseLong(expMatcher.group(1));
        if (Instant.now().getEpochSecond() >= expSeconds) {
            throw new UnauthorizedException("JWT token has expired.");
        }

        Matcher subMatcher = SUB_PATTERN.matcher(payloadJson);
        if (!subMatcher.find() || subMatcher.group(1).isBlank()) {
            throw new UnauthorizedException("JWT token is missing subject.");
        }
        return subMatcher.group(1);
    }

    private byte[] hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretBytes, "HmacSHA256"));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Failed to compute HMAC-SHA256 signature", ex);
        }
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
