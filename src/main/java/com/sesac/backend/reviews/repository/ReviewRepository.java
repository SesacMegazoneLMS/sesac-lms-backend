package com.sesac.backend.reviews.repository;

import com.sesac.backend.reviews.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByCourse_Id(Long courseId, Pageable pageable);

    List<Review> findByCourse_Id(Long courseId);
}
