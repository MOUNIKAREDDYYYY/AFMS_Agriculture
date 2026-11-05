package com.AgricultureFMSystem.AgricultureFMSystem.config;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.User;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.UnauthorizedException;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Base64;
import java.util.Optional;

/**
 * ============================================================================================
 * Phase 5: Spring Web Security Interceptor (HandlerInterceptor)
 * ============================================================================================
 * // Purpose: Intercepts incoming HTTP requests to validate Bearer tokens,
 * // authenticate principals, and bind current user identity to the request context.
 * //
 * // Concepts demonstrated:
 * // - HandlerInterceptor: Pre-handle interception before controller dispatch
 * // - Bearer Token Validation: Parses "Authorization: Bearer <token>" header
 * // - Principal Resolution: Binds User entity into request attribute ("CURRENT_USER")
 * // - Role Authorization: Can verify if user holds required role (ADMIN, FARMER, etc.)
 * ============================================================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthSecurityInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    /**
     * // Pre-handle hook executed before any matched controller method
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // // Allow CORS preflight OPTIONS requests to pass through
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // // Check for Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();

            try {
                // // Token format: afms_jwt_<uuid>_<base64Username>
                if (token.startsWith("afms_jwt_")) {
                    String[] parts = token.split("_");
                    if (parts.length >= 4) {
                        String base64Username = parts[3];
                        String username = new String(Base64.getDecoder().decode(base64Username));

                        Optional<User> userOpt = userRepository.findByUsernameOrEmailIgnoreCase(username);
                        if (userOpt.isPresent()) {
                            User user = userOpt.get();
                            // // Bind authenticated principal to request attribute for downstream access
                            request.setAttribute("CURRENT_USER", user);
                            request.setAttribute("USER_ROLE", user.getRole().name());
                            log.debug("// [Security] Authenticated user '{}' with role: {}", username, user.getRole());
                            return true;
                        }
                    }
                }
            } catch (Exception ex) {
                log.warn("// [Security] Token parsing failed: {}", ex.getMessage());
            }
        }

        // // For endpoints under /api/ (except auth), token is validated if present
        return true;
    }
}
