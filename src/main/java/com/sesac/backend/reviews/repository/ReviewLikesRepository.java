package com.sesac.backend.reviews.repository;

import com.sesac.backend.reviews.domain.Review;
import com.sesac.backend.reviews.domain.ReviewLikes;
import com.sesac.backend.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikesRepository extends JpaRepository<ReviewLikes, Long> {
    // userId와 reviewId를 통해 매칭되는 likes entity의 상태 조회
    Optional<ReviewLikes> findByUserIdAndReviewId(User user, Review review);

    // 특정 수강평에 대한 likes entity totalCount 계산
    int countByReviewIdAndLikedIsTrue(Review review);
}
