package com.AgricultureFMSystem.AgricultureFMSystem.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ============================================================================================
 * Phase 2, 4 & 5: Spring MVC Web Configuration (CORS, Interceptors, Static Uploads)
 * ============================================================================================
 * // Purpose: Configures WebMvcConfigurer for:
 * // - Security & Auth HandlerInterceptors
 * // - CORS (Cross-Origin Resource Sharing) for React/Angular/Vue frontends
 * // - Static resource handling for uploaded documents
 * ============================================================================================
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthSecurityInterceptor authSecurityInterceptor;

    /**
     * // Register security interceptor for API routes
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("// [WebMvcConfig] Registering AuthSecurityInterceptor for /api/** routes");
        registry.addInterceptor(authSecurityInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/actuator/**"
                );
    }

    /**
     * // Configure Global CORS mapping
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("// [WebMvcConfig] Configuring CORS policies for all endpoints");
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    /**
     * // Map uploads directory as a static resource path
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
