package com.sesac.backend.enrollments.controller;

import com.sesac.backend.enrollments.dto.response.EnrollmentResponse;
import com.sesac.backend.enrollments.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequestMapping("/enrollments")
@RestController
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("")
    public ResponseEntity<?> getAllEnrollments(Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        try {
            List<EnrollmentResponse> enrolledCourses = enrollmentService.getEnrollmentsByUserUuid(userId);

            if (enrolledCourses.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "message", "수강 중인 강의가 없습니다",
                        "courses", enrolledCourses
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "수강 중인 강의 목록을 성공적으로 불러왔습니다",
                    "courses", enrolledCourses
            ));

        } catch (Exception e) {
            log.error("강의 목록 조회 실패", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "강의 목록을 불러오는데 실패했습니다",
                    "error", e.getMessage()
            ));
        }

    }

}
