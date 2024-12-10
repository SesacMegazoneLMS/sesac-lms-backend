package com.sesac.backend.reviews.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.reviews.domain.Review;
import com.sesac.backend.reviews.dto.request.ReviewRequest;
import com.sesac.backend.reviews.repository.ReviewRepository;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public void saveReview(ReviewRequest req, UUID USER_ID) {

        User user = userRepository.findByUserId(USER_ID).orElseThrow();
        Course course = courseRepository.findById(req.getCourseId()).orElseThrow();

        Review review = Review.builder()
                .writer(user)
                .course(course)
                .content(req.getContent())
                .rating(req.getRating())
                .build();

        reviewRepository.save(review);
    }
}
