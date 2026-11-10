package com.AgricultureFMSystem.AgricultureFMSystem.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Generates interactive OpenAPI / Swagger UI documentation at /swagger-ui/index.html
 * with Bearer JWT authentication support.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    @Value("${server.port:8080}")
    private int serverPort = 8080;

    @Bean
    public OpenAPI agricultureOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Agriculture Farm Management System (AFMS) API")
                        .description("Comprehensive RESTful API for Farmers to manage crops, fields, expenses, " +
                                "scheduled agricultural activities, and harvest records.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("AFMS Agronomy & Tech Support")
                                .email("support@agriculturefms.com")
                                .url("https://www.agriculturefms.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME))
                .components(new Components().addSecuritySchemes(
                        BEARER_SCHEME_NAME,
                        new SecurityScheme()
                                .name(BEARER_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste JWT token returned by POST /api/auth/login")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Agriculture Farm Management Project Guide")
                        .url("https://github.com/AgricultureFMSystem"));
    }
}
