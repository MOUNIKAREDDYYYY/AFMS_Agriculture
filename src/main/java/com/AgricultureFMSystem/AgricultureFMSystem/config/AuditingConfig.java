package com.AgricultureFMSystem.AgricultureFMSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * ============================================================================================
 * Phase 3 & 4: JPA Auditing Configuration
 * ============================================================================================
 * Enables automatic population of @CreatedDate and @LastModifiedDate in BaseEntity.
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfig {
}
