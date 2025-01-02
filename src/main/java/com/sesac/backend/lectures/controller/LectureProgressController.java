package com.sesac.backend.lectures.controller;

import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.RequiredArgsConstructor;
import com.sesac.backend.lectures.service.LectureProgressService;
import com.sesac.backend.audit.CurrentUser;
import com.sesac.backend.lectures.dto.request.ProgressRequest;
import com.sesac.backend.lectures.dto.response.StudentStatsResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/lectures")
@RequiredArgsConstructor
@Slf4j
public class LectureProgressController {
    private final LectureProgressService progressService;
    private final UserRepository userRepository;

    @PostMapping("/{lectureId}/progress")
    public ResponseEntity<Void> saveProgress(
        @PathVariable Long lectureId,
        @RequestBody ProgressRequest request,
        @CurrentUser UUID USER_ID
    ) {
        try {
            
            log.info("Processing progress update for user: {}", USER_ID);

            User student = userRepository.findByUuid(USER_ID)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

            log.info("Found student: {}", student.getUuid());
            
            progressService.saveProgress(student, lectureId, request.getProgressRate(), request.getWatchedSeconds());
            return ResponseEntity.ok().build();
            
        } catch (EntityNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error saving progress: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/students/stats")
    public ResponseEntity<StudentStatsResponse> getStudentStats(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            UUID userUuid = UUID.fromString(userDetails.getUsername());
            int totalSeconds = progressService.getTotalWatchedSeconds(userUuid);
            long completedLectures = progressService.getCompletedLecturesCount(userUuid);

            StudentStatsResponse stats = new StudentStatsResponse(
                totalSeconds / 3600,  // 초를 시간으로 변환
                0,  // 주간 학습 시간은 별도 계산 필요
                completedLectures
            );
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting student stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
