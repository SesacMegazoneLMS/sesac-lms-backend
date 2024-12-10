package com.sesac.backend.reviews.controller;

import com.sesac.backend.audit.CurrentUser;
import com.sesac.backend.reviews.domain.Review;
import com.sesac.backend.reviews.dto.request.ReviewRequest;
import com.sesac.backend.reviews.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequestMapping("/reviews")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 수강평 추가
    @PostMapping("")
    public ResponseEntity<String> createReview(@RequestBody ReviewRequest req, @CurrentUser UUID USER_ID) {
        try{
            reviewService.saveReview( req, USER_ID );
            return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}
