package com.sesac.backend.orders.repository;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.orders.domain.OrderedCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderedCoursesRepository extends JpaRepository<OrderedCourses, Long> {

    List<OrderedCourses> findAllByCourse(Course course);

    boolean existsByCourse(Course course);

    @Query("SELECT SUM(e.price) FROM OrderedCourses e WHERE e.course.id IN :ids")
    BigDecimal sumPriceByIds(@Param("ids") List<Long> ids);

    @Query("SELECT COUNT(oc) FROM OrderedCourses oc " +
            "WHERE oc.course.id IN :sortedCourseIds " +
            "AND EXTRACT(YEAR FROM oc.createdAt) = :year " +
            "AND EXTRACT(MONTH FROM oc.createdAt) = :month")
    Integer countNewEnrollmentsForMonth(
            @Param("sortedCourseIds") List<Long> sortedCourseIds,
            @Param("year") int year,
            @Param("month") int month);

    @Query("SELECT SUM(oc.price) FROM OrderedCourses oc " +
            "WHERE oc.course.id IN :sortedCourseIds " +
            "AND EXTRACT(YEAR FROM oc.createdAt) = :year " +
            "AND EXTRACT(MONTH FROM oc.createdAt) = :month")
    BigDecimal calculateMonthlyRevenue(
            @Param("sortedCourseIds") List<Long> sortedCourseIds,
            @Param("year") int year,
            @Param("month") int month);

}
