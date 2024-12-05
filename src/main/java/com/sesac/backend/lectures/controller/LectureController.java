package com.sesac.backend.lectures.controller;

import com.sesac.backend.lectures.dto.request.LectureRequest;
import com.sesac.backend.lectures.dto.response.LectureResponse;
import com.sesac.backend.lectures.service.LectureService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/lectures")
public class LectureController {

    private final LectureService lectureService;

    @PostMapping("/video/complete")
    public ResponseEntity<Void> handleTranscodingComplete(
        @RequestBody VideoCompleteRequest request
    ) {
        lectureService.updateVideoStatus(
            request.getVideoKey(),
            request.getHlsUrl(),
            request.getStatus()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<LectureResponse> createLecture(@RequestBody LectureRequest request) {
        return ResponseEntity.ok(lectureService.createLecture(request));
    }
}

@Getter
@Setter
class VideoCompleteRequest {
    private String videoKey;
    private String hlsUrl;
    private String status;
}
