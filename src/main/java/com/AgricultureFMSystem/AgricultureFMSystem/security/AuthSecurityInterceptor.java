package com.AgricultureFMSystem.AgricultureFMSystem.security;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.User;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.UnauthorizedException;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

/**
 * Intercepts API requests to validate signed JWT Bearer tokens and bind the
 * authenticated {@link User} and role to request attributes.
 */
@Slf4j
@Component
public class AuthSecurityInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${app.security.require-auth:false}")
    private boolean requireAuth = false;

    public AuthSecurityInterceptor(UserRepository userRepository) {
        this(userRepository, new JwtTokenUtil());
    }

    @Autowired
    public AuthSecurityInterceptor(UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            if (requireAuth) {
                log.warn("[Security] Missing Authorization header on {} {}", method, path);
                throw new UnauthorizedException("Authorization Bearer token is required.");
            }
            return true;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.warn("[Security] Invalid Authorization scheme on {} {}", method, path);
            throw new UnauthorizedException("Authorization header must use Bearer token scheme.");
        }

        String token = authHeader.substring(7).trim();
        User user = validateAndResolveUser(token, path);

        request.setAttribute("CURRENT_USER", user);
        request.setAttribute("USER_ROLE", user.getRole().name());
        log.debug("[Security] Authenticated user '{}' with role: {}", user.getUsername(), user.getRole());
        return true;
    }

    private User validateAndResolveUser(String token, String path) {
        String username = jwtTokenUtil.validateAndExtractUsername(token);

        Optional<User> userOpt = userRepository.findByUsernameOrEmailIgnoreCase(username);
        User user = userOpt.orElseThrow(() -> {
            log.warn("[Security] Unknown principal '{}' on path {}", username, path);
            return new UnauthorizedException("Invalid or expired authentication token.");
        });

        if (Boolean.FALSE.equals(user.getActive())) {
            log.warn("[Security] Disabled user '{}' attempted access on path {}", username, path);
            throw new UnauthorizedException("User account is disabled.");
        }

        return user;
    }
}
