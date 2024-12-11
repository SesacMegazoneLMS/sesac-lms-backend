package com.sesac.backend.reviews.controller;

import com.sesac.backend.audit.CurrentUser;
import com.sesac.backend.reviews.domain.Review;
import com.sesac.backend.reviews.dto.request.ReviewRequest;
import com.sesac.backend.reviews.dto.response.ReviewResponse;
import com.sesac.backend.reviews.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequestMapping("/reviews")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 수강평 추가
    @PostMapping("")
    public ResponseEntity<String> createReview(@RequestBody ReviewRequest req, @CurrentUser UUID uuid) {
        try{
            reviewService.saveReview( req, uuid );
            return ResponseEntity.ok("수강평이 성공적으로 등록되었습니다.");
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    // 강의별 목록 출력은 course controller에 있습니다
    // 모든 수강평 목록 출력
    @GetMapping("")
    public ResponseEntity<?> getAllReviews(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        try{
            List<ReviewResponse> allReviews = reviewService.getAllReviews(page, size);

            if( allReviews.isEmpty() ){
                return ResponseEntity.ok(Map.of(
                   "message", "수강평이 없습니다.",
                    "reviews", allReviews
                ));
            }

            return ResponseEntity.ok(Map.of(
                "message", "수강평 목록 호출 성공",
                "reviews", allReviews
            ));
        }catch (EntityNotFoundException e){
            return ResponseEntity.noContent().build();
        }
    }

    // 수강평 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId, @RequestBody ReviewRequest req, @CurrentUser UUID uuid) {
        try{
            reviewService.updateReview( reviewId, req, uuid );
            return ResponseEntity.ok("수강평이 성공적으로 수정되었습니다. no:" + reviewId);
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    // 수강평 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId, @CurrentUser UUID uuid){
        try{
            reviewService.deleteReview( reviewId, uuid );
            return ResponseEntity.ok("수강평이 성공적으로 삭제되었습니다.");
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

}
