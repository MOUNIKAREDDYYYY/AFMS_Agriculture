package com.AgricultureFMSystem.AgricultureFMSystem.scheduler;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.Activity;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * ============================================================================================
 * Phase 4: Spring Boot Task Scheduler (@Scheduled)
 * ============================================================================================
 * Periodically monitors pending farming tasks, identifies overdue activities, and logs
 * alerts for farm supervisors.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FarmTaskScheduler {

    private final ActivityRepository activityRepository;

    /**
     * Runs every hour (fixedRate = 3600000 ms, initialDelay = 10000 ms) to inspect overdue activities.
     */
    @Scheduled(initialDelay = 10000, fixedRate = 3600000)
    @Transactional(readOnly = true)
    public void monitorOverdueActivities() {
        log.info("[Scheduler] Running automated check for overdue and upcoming farming activities...");

        LocalDate today = LocalDate.now();
        List<Activity> pendingActivities = activityRepository.findPendingActivities();

        long overdueCount = 0;
        long dueTodayCount = 0;

        for (Activity activity : pendingActivities) {
            if (activity.getScheduledDate().isBefore(today)) {
                overdueCount++;
                log.warn("[Scheduler Alert] OVERDUE Activity ID: {}, Title: '{}', Scheduled: {}, Field: '{}'",
                        activity.getId(), activity.getTitle(), activity.getScheduledDate(),
                        activity.getField() != null ? activity.getField().getName() : "Unknown");
            } else if (activity.getScheduledDate().isEqual(today)) {
                dueTodayCount++;
                log.info("[Scheduler Reminder] Activity DUE TODAY: ID: {}, Title: '{}', Assigned To: '{}'",
                        activity.getId(), activity.getTitle(), activity.getAssignedTo());
            }
        }

        log.info("[Scheduler] Check complete. Overdue activities: {}, Due today: {}, Total pending: {}",
                overdueCount, dueTodayCount, pendingActivities.size());
    }
}
