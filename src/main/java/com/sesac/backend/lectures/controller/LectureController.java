package com.sesac.backend.lectures.controller;

import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.lectures.dto.request.LectureRequest;
import com.sesac.backend.lectures.dto.request.VideoCompleteRequest;
import com.sesac.backend.lectures.dto.response.LectureDetailResponse;
import com.sesac.backend.lectures.dto.response.LectureResponse;
import com.sesac.backend.lectures.service.LectureService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 로그를 위한 Slf4j 어노테이션
@Slf4j
// REST 컨트롤러로 지정
@RestController
// 모든 필드를 포함하는 생성자 생성
@AllArgsConstructor
// "/lectures" 경로로 매핑
@RequestMapping("/lectures")
public class LectureController {

    // 강의 서비스 의존성 주입
    private final LectureService lectureService;

    // 비디오 변환 완료 요청을 처리하는 엔드포인트
    @PostMapping("/video/complete")
    public ResponseEntity<Void> handleTranscodingComplete(
        @RequestBody VideoCompleteRequest request
    ) {
    try {    
        log.info("Received video complete request: {}", request);
        lectureService.updateVideoStatus(
            request.getLectureId(),
            request.getVideoKey(),
            request.getStatus(),
            request.getDuration()
        );
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        log.error("Error updating video status: {}", e.getMessage(), e);
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.internalServerError().build();
    }
}

    // 강의 생성 요청을 처리하는 엔드포인트
    @PostMapping
    public ResponseEntity<LectureResponse> createLecture(@RequestBody LectureRequest request) {
        return ResponseEntity.ok(lectureService.createLecture(request));
    }

    // 강의 수정 엔드포인트
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateLecture(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            lectureService.updateLecture(id, updates);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("강의 수정 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 단일 강의 조회
    @GetMapping("/{id}")
    public ResponseEntity<LectureDetailResponse> getLecture(@PathVariable Long id) {
        try {
            Lecture lecture = lectureService.getLecture(id);
            return ResponseEntity.ok(LectureDetailResponse.from(lecture));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 전체 강의 목록 조회
    @GetMapping
    public ResponseEntity<List<LectureDetailResponse>> getAllLectures() {
        List<LectureDetailResponse> lectures = lectureService.getAllLectures()
            .stream()
            .map(LectureDetailResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lectures);
    }

    // 특정 코스의 강의 목록 조회
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LectureDetailResponse>> getLecturesByCourse(@PathVariable Long courseId) {
        List<LectureDetailResponse> lectures = lectureService.getLecturesByCourseId(courseId)
            .stream()
            .map(LectureDetailResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lectures);
    }

    // 강의 삭제 엔드포인트
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecture(@PathVariable Long id) {
        try {
            lectureService.deleteLecture(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("강의 삭제 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
