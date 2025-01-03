package com.sesac.backend.statistics.scheduler;

import com.sesac.backend.statistics.service.InstructorStatsService;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.enums.UserType;
import com.sesac.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsUpdateScheduler {

    private final InstructorStatsService instructorStatsService;
    private final UserRepository userRepository;

    // 테스트용: 1분마다 실행
    @Scheduled(fixedRate = 3600000)
    public void updateTestStats() {
        log.info("Starting test statistics update at {}", LocalDateTime.now());
        try {
            instructorStatsService.updateInstructorStats();
            instructorStatsService.updateAllInstructorsMonthlyStats();
            log.info("Completed test statistics update at {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error during test statistics update", e);
        }
    }
}
