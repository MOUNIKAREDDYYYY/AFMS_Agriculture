package com.AgricultureFMSystem.AgricultureFMSystem.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * ============================================================================================
 * Phase 4: OpenAPI / Swagger Documentation Configuration
 * ============================================================================================
 * Generates interactive REST API documentation and sandbox UI at /swagger-ui/index.html.
 * Covers endpoints for Farmers, Farms, Fields, Crops, Activities, Expenses, Harvests, and Reports.
 */
@Configuration
public class OpenApiConfig {

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
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Agriculture Farm Management Project Guide")
                        .url("https://github.com/AgricultureFMSystem"));
    }
}
