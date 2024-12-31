package com.sesac.backend.statistics.controller;

import com.sesac.backend.statistics.service.InstructorStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InstructorStatsController {

    private final InstructorStatsService instructorStatsService;

    @GetMapping("/instructor/stats")
    public ResponseEntity<?> getStats(Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        try {
            return ResponseEntity.ok(Map.of(
                    "message", "통계 로드 성공",
                    "statistics", instructorStatsService.getInstructorStats(userId)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/instructor/stats/manual-update")
    public ResponseEntity<?> manualUpdate() {
        instructorStatsService.updateAllInstructorsMonthlyStats();
        return ResponseEntity.ok("Statistics updated successfully");
    }
}
