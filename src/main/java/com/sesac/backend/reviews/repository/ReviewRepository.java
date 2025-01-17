package com.sesac.backend.reviews.repository;

import com.sesac.backend.reviews.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByCourse_Id(Long courseId, Pageable pageable);

    List<Review> findByCourse_Id(Long courseId);

    @Query("SELECT AVG(e.rating) FROM Review e WHERE e.course.id IN :ids")
    Double averageRatingByCourseIds(@Param("ids") List<Long> ids);

    @Query("SELECT AVG(e.rating) FROM Review e " +
            "WHERE e.course.id IN :courseIds " +
            "AND EXTRACT(YEAR FROM e.createdAt) = :year " +
            "AND EXTRACT(MONTH FROM e.createdAt) = :month")
    Double averageRatingForMonth(
            @Param("courseIds") List<Long> courseIds,
            @Param("year") int year,
            @Param("month") int month);

}
