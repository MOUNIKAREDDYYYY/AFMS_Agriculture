package com.AgricultureFMSystem.AgricultureFMSystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * ============================================================================================
 * Phase 5: Spring Boot Context Load Test with Test Profile
 * ============================================================================================
 * Uses @ActiveProfiles("test") to load configuration from application-test.properties,
 * ensuring test isolation with an in-memory database without mutating live MySQL data.
 */
@SpringBootTest
@ActiveProfiles("test")
class AgricultureFmSystemApplicationTests {

	@Test
	void contextLoads() {
	}

}
