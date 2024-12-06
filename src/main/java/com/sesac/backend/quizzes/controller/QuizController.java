package com.sesac.backend.quizzes.controller;

import com.sesac.backend.quizzes.dto.request.QuizCreationRequest;
import com.sesac.backend.quizzes.dto.response.QuizCreationResponse;
import com.sesac.backend.quizzes.service.QuizService;
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
}
