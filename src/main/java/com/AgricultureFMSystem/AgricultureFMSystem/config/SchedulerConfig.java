package com.AgricultureFMSystem.AgricultureFMSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ============================================================================================
 * Phase 4: Spring Task Scheduler Configuration
 * ============================================================================================
 * Enables asynchronous and scheduled tasks (cron and fixedRate jobs) for automated agricultural
 * reminders (e.g. upcoming irrigation, overdue activities, harvest season notifications).
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
}
