package com.sesac.backend.lectures.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.RequiredArgsConstructor;
import com.sesac.backend.lectures.service.LectureProgressService;
import com.sesac.backend.lectures.dto.request.ProgressRequest;
import com.sesac.backend.lectures.dto.response.StudentStatsResponse;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LectureProgressController {
    private final LectureProgressService progressService;

    @PostMapping("/lectures/{lectureId}/progress")
    public ResponseEntity<Void> saveProgress(
        @PathVariable Long lectureId,
        @RequestBody ProgressRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        progressService.saveProgress(
            lectureId, 
            userDetails.getUsername(), 
            request.getProgressRate().doubleValue(),
            request.getWatchedSeconds()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/students/stats")
    public ResponseEntity<StudentStatsResponse> getStudentStats(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userUuid = UUID.fromString(userDetails.getUsername());
        int totalSeconds = progressService.getTotalWatchedSeconds(userUuid);
        long completedLectures = progressService.getCompletedLecturesCount(userUuid);

        StudentStatsResponse stats = new StudentStatsResponse(
            totalSeconds / 3600,  // 초를 시간으로 변환
            0,  // 주간 학습 시간은 별도 계산 필요
            completedLectures
        );
        return ResponseEntity.ok(stats);
    }
}
