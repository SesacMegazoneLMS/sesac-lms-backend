package com.sesac.backend.statistics.scheduler;

import com.sesac.backend.statistics.service.InstructorStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsUpdateScheduler {

    private final InstructorStatsService instructorStatsService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void updateDailyStats() {
        log.info("Starting daily statistics update at {}", LocalDateTime.now());
        try {
            instructorStatsService.updateAllInstructorsMonthlyStats();
            log.info("Completed daily statistics update at {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error during daily statistics update", e);
        }
    }
}
