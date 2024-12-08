package com.sesac.backend.quizzes.controller;

import com.sesac.backend.audit.CurrentUser;
import com.sesac.backend.quizzes.dto.request.QuizCreationRequest;
import com.sesac.backend.quizzes.dto.response.QuizCreationResponse;
import com.sesac.backend.quizzes.dto.response.QuizDetailResponse;
import com.sesac.backend.quizzes.dto.response.QuizReadResponse;
import com.sesac.backend.quizzes.service.QuizService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/quizzes")
@RestController
public class QuizController {

    private final QuizService quizService;

    @PostMapping("")
    public ResponseEntity<QuizCreationResponse> createQuiz(
        @RequestBody final QuizCreationRequest quizCreationRequest) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(quizService.createQuiz(quizCreationRequest));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<List<QuizReadResponse>> findMyQuizzes(
        @RequestParam final Long courseId,
        @CurrentUser final UUID userId
    ) {
        try {
            return ResponseEntity.ok(quizService.findMyQuizzes(courseId, userId));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDetailResponse> findQuizDetail(
        @PathVariable final Long quizId,
        @CurrentUser final UUID userId
    ) {
        try {
            return ResponseEntity.ok(quizService.findQuizDetail(quizId, userId));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(
        @PathVariable final Long quizId,
        @CurrentUser final UUID userId
    ) {
        try {
            quizService.deleteQuiz(quizId, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
