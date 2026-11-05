package com.AgricultureFMSystem.AgricultureFMSystem.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * ============================================================================================
 * Phase 5: Spring Caching Configuration (@EnableCaching)
 * ============================================================================================
 * // Purpose: Configures high-performance in-memory caching to eliminate repetitive database queries
 * // for catalog, crop types, and farmer profiles.
 * //
 * // Concepts demonstrated:
 * // - @EnableCaching: Enables Spring's annotation-driven cache management capability
 * // - CacheManager: Abstraction over the underlying cache storage (ConcurrentMapCacheManager for Dev)
 * // - Named Caches: "crops", "farmers", "farms", "fieldHistory"
 * // - Cache Invalidation: Evicts stale data upon mutation (@CacheEvict)
 * ============================================================================================
 */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * // Defines the CacheManager bean with pre-configured agricultural cache regions
     */
    @Bean
    public CacheManager cacheManager() {
        log.info("// [CacheConfig] Initializing ConcurrentMapCacheManager for Agricultural Farm System");

        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        // // Pre-seed dedicated cache regions
        cacheManager.setCacheNames(List.of(
                "crops",         // Frequently accessed crop varieties and optimal growth parameters
                "farmers",       // Registered farmer identity and contact information
                "farms",         // Farm geographical boundaries and soil characteristics
                "fieldHistory"   // Field lifecycle histories
        ));

        // // Allow dynamic cache generation if other caches are referenced at runtime
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }
}
