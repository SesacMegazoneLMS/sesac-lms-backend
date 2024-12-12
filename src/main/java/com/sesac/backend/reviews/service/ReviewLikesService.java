package com.sesac.backend.reviews.service;

import com.sesac.backend.reviews.domain.Review;
import com.sesac.backend.reviews.domain.ReviewLikes;
import com.sesac.backend.reviews.repository.ReviewLikesRepository;
import com.sesac.backend.reviews.repository.ReviewRepository;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewLikesService {

    private final ReviewLikesRepository reviewLikesRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    // likes entity 생성 or 상태 변경
    @Transactional
    public boolean activeLikes(Long reviewId, UUID userId) {
        User user = userRepository.findByUuid(userId).orElseThrow(
            () -> new RuntimeException("존재하지 않는 사용자입니다.")
        );

        Review review = reviewRepository.findById(reviewId).orElseThrow(
            () -> new RuntimeException("존재하지 않는 수강평입니다.")
        );

        // 해당 사용자와 해당 수강평에 대한 좋아요 레코드 찾고 없으면 새 레코드 생성
        return reviewLikesRepository.findByUserIdAndReviewId(user, review)
            .map(existingLike -> {
                boolean newLikedStatus = !existingLike.isLiked(); // 좋아요 상태 반전
                existingLike.setLiked(newLikedStatus); // 상태 업데이트
                reviewLikesRepository.save(existingLike); // 변경된 상태 저장
                return newLikedStatus; // 새로운 좋아요 상태 반환
            })
            .orElseGet(() -> {
                ReviewLikes newReviewLikes = ReviewLikes.builder()
                    .userId(user)
                    .reviewId(review)
                    .liked(true) // 새로 생성된 좋아요는 항상 true
                    .build();
                reviewLikesRepository.save(newReviewLikes); // 새 좋아요 저장
                return true; // 새로 생성된 좋아요 상태 반환
            });
    }

//    // 조회 시 상태 반환
//    public Map<String, Boolean> getLikesStatus(Long reviewId, UUID userId) {
//        // 로그인하지 않은 경우 (userId가 null이거나 0인 경우)
//        if (userId == null) {
//            return Map.of("status", false);
//        }
//
//        User user = userRepository.findByUuid(userId)
//            .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
//
//        Review review = reviewRepository.findById(reviewId)
//            .orElseThrow(() -> new RuntimeException("존재하지 않는 수강평입니다."));
//
//        // 해당 사용자와 리뷰에 대한 좋아요 레코드 찾기
//        Optional<ReviewLikes> reviewLikes = reviewLikesRepository.findByUserIdAndReviewId(user, review);
//
//        return Map.of("status", reviewLikes.isPresent() && reviewLikes.get().isLiked());
//    }

    // 조회 시 상태 반환
    public boolean getLikesStatus(Long reviewId, UUID userId) {
        // 로그인하지 않은 경우 (userId가 null인 경우)
        if (userId == null) {
            return false;
        }

        User user = userRepository.findByUuid(userId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 수강평입니다."));

        // 해당 사용자와 리뷰에 대한 좋아요 레코드 찾기
        Optional<ReviewLikes> reviewLikes = reviewLikesRepository.findByUserIdAndReviewId(user, review);

        return reviewLikes.isPresent() && reviewLikes.get().isLiked();
    }

    // likes entity totalCount 계산
    public int countLikesByReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
            () -> new RuntimeException("존재하지 않는 수강평입니다.")
        );

        return reviewLikesRepository.countByReviewIdAndLikedIsTrue(review);
    }
}
