package com.sesac.backend.lectures.controller;

import com.sesac.backend.lectures.dto.request.LectureRequest;
import com.sesac.backend.lectures.dto.response.LectureResponse;
import com.sesac.backend.lectures.service.LectureService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    log.info("Received video complete request: {}", request);
    try {
        lectureService.updateVideoStatus(
                request.getLectureId(),
            request.getVideoKey(),
            request.getHlsUrl(),
            request.getStatus()
        );
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        log.error("Error updating video status: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    // 강의 생성 요청을 처리하는 엔드포인트
    @PostMapping
    public ResponseEntity<LectureResponse> createLecture(@RequestBody LectureRequest request) {
        // 강의 생성 후 응답 반환
        return ResponseEntity.ok(lectureService.createLecture(request));
    }
}

// 비디오 완료 요청을 위한 DTO 클래스
@Getter
@Setter
class VideoCompleteRequest {
    private Long lectureId;
    private String videoKey;
    private String hlsUrl;
    private String status;
}