package com.sesac.backend.reviews.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.reviews.domain.Review;
import com.sesac.backend.reviews.dto.request.ReviewRequest;
import com.sesac.backend.reviews.dto.response.ReviewResponse;
import com.sesac.backend.reviews.dto.response.ReviewStatus;
import com.sesac.backend.reviews.repository.ReviewRepository;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    // 수강평 등록
    public void saveReview(ReviewRequest req, UUID uuid) {

        User user = userRepository.findByUuid(uuid).orElseThrow();
        Course course = courseRepository.findById(req.getCourseId()).orElseThrow();

        Review review = Review.builder()
                .writer(user)
                .course(course)
                .content(req.getContent())
                .rating(req.getRating())
                .build();

        reviewRepository.save(review);
    }

    // 수강평 페이지 용 getAll
    public List<ReviewResponse> getAllReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);

        Page<Review> allReviews = reviewRepository.findAll(pageable);

        List<ReviewResponse> res = new ArrayList<>();

        for (Review review : allReviews){
            res.add(ReviewResponse.builder()
                    .id(review.getId())
                    .writer(review.getWriter().getNickname())
                    .content(review.getContent())
                    .rating(review.getRating())
                    .likes(review.getLikes())
                    .helpful(review.getHelpful())
                    .build());
        }

        return res;
    }

    // 강좌별 수강평 목록 출력
    public List<ReviewResponse> getReviews(Long courseId, int page, int size){
        // 강좌 존재 여부 확인
        if (!courseRepository.existsById(courseId)) {
            throw new EntityNotFoundException("존재하지 않는 강좌입니다."); // 예외 처리
        }

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Review> reviews = reviewRepository.findByCourse_Id(courseId, pageable);

        List<ReviewResponse> res = new ArrayList<>();

        for(Review review : reviews){
            res.add(ReviewResponse.builder()
                .id(review.getId())
                .writer(review.getWriter().getNickname())
                .content(review.getContent())
                .rating(review.getRating())
                .likes(review.getLikes())
                .helpful(review.getHelpful())
                .build());
        }

        return res;
    }

    // 수강평 수정----------------------------------------------
    public void updateReview(Long reviewId, ReviewRequest req, UUID uuid) {
        try {
            User user = userRepository.findByUuid(uuid).orElseThrow(
                () -> new NoSuchElementException("로그인을 해주세요"));

            Review targetReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 수강평이 존재하지 않습니다."));

            if (!targetReview.getWriter().getId().equals(user.getId())) {
                throw new IllegalArgumentException("권한이 없습니다.");
            }

            // 리뷰 수정
            updateReviewFields(targetReview, req, user);

            // 수정된 리뷰 저장
            reviewRepository.save(targetReview);
        } catch (NoSuchElementException e) {
            handleException(e, "User not found");
        } catch (IllegalArgumentException e) {
            handleException(e, "Review not found or Access denied");
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("예상치 못한 오류가 발생했습니다.", e);
        }
    }

    // db 조회해서 해당 값에 변화가 있을 시에 값 수정하는 메서드
    private void updateReviewFields(Review targetReview, ReviewRequest req, User user) {
        if (!targetReview.getContent().equals(req.getContent())) {
            targetReview.setContent(req.getContent()); // 리뷰 내용 수정
        }
        if (targetReview.getRating().equals(req.getRating())) {
            targetReview.setRating(req.getRating());   // 평점 수정
        }
        // 닉네임 변경 체크
        if (targetReview.getWriter().getId().equals(user.getId()) && !targetReview.getWriter().getNickname().equals(user.getNickname())) {
            targetReview.getWriter().setNickname(user.getNickname()); // 닉네임 수정
        }
    }
    //--------------------------------------------------------------

    // 수강평 삭제-------------------------------------------------
    public void deleteReview(Long reviewId, UUID uuid) {
        try {
            User user = userRepository.findByUuid(uuid).orElseThrow(
                () -> new NoSuchElementException("로그인을 해주세요"));

            Review targetReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 수강평이 존재하지 않습니다."));

            if (!targetReview.getWriter().getId().equals(user.getId())) {
                throw new IllegalArgumentException("권한이 없습니다.");
            }

            reviewRepository.delete(targetReview);

        } catch (NoSuchElementException e) {
            handleException(e, "User not found");
        } catch (IllegalArgumentException e) {
            handleException(e, "Review not found or Access denied");
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("예상치 못한 오류가 발생했습니다.", e); // 일반 예외는 새로운 런타임 예외로 감싸서 던짐
        }
    }

    // 강의별 수강 점수, 총점에 관한 info
    public ReviewStatus getScoresInfo(Long courseId){
        List<Review> reviewList = reviewRepository.findByCourse_Id(courseId);

        // 수강평 수
        int reviewCount = reviewList.size();

        // 리뷰 점수 총합
        int totalScore = reviewList.stream()
                                        .mapToInt(Review::getRating)
                                        .sum();

        // 평균 점수 계산
        double averageRating = reviewCount > 0 ? (double) totalScore/reviewCount : 0.0;

        return ReviewStatus.builder()
            .reviewCount(reviewCount)
            .averageRating(averageRating)
            .build();
    }

    // 예외처리 중복코드 방지 메서드
    private void handleException(Exception e, String logMessage) {
        logger.error("{}: {}", logMessage, e.getMessage(), e);
        throw new RuntimeException(logMessage, e); // 예외를 다시 던져서 컨트롤러에서 처리하도록 함
    }
}
