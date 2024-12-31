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

    // 테스트용: 1분마다 실행
    @Scheduled(fixedRate = 60000)
    public void updateTestStats() {
        log.info("Starting test statistics update at {}", LocalDateTime.now());
        try {
            instructorStatsService.updateAllInstructorsMonthlyStats();
            log.info("Completed test statistics update at {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error during test statistics update", e);
        }
    }
}
