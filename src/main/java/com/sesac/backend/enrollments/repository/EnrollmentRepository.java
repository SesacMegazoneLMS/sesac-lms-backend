package com.sesac.backend.enrollments.repository;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.enrollments.domain.Enrollment;
import com.sesac.backend.enrollments.dto.response.RecentEnrollmentDto;
import com.sesac.backend.orders.constants.OrderedCoursesStatus;
import com.sesac.backend.orders.domain.OrderedCourses;
import java.util.List;

import com.sesac.backend.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findAllByOrderedCoursesCourse(Course course);

    Optional<Enrollment> findByOrderedCourses(OrderedCourses orderedcourses);

    boolean existsByOrderedCourses(OrderedCourses orderedcourses);

    @Query("SELECT DISTINCT e FROM Enrollment e " +
            "JOIN FETCH e.orderedCourses oc " +
            "JOIN FETCH oc.course c " +
            "LEFT JOIN FETCH c.lectures l " +
            "WHERE e.user.uuid = :userUuid " +
            "AND e.isActive = true " +
            "AND oc.status = :status " +
            "ORDER BY l.orderIndex ASC")
    List<Enrollment> findActiveEnrollmentsWithCoursesByUserUuid(
            @Param("userUuid") UUID userUuid,
            @Param("status") OrderedCoursesStatus status
    );

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.orderedCourses.course.id IN :courseIds")
    Integer countByOrderedCoursesIdIn(@Param("courseIds") List<Long> courseIds);

//    @Query("SELECT COUNT(e) FROM Enrollment e " +
//            "WHERE e.orderedCourses.course.id IN :sortedCourseIds " +
//            "AND EXTRACT(YEAR FROM e.createdAt) = :year " +
//            "AND EXTRACT(MONTH FROM e.createdAt) = :month")
//    Integer countNewEnrollmentsForMonth(
//            @Param("sortedCourseIds") List<Long> sortedCourseIds,
//            @Param("year") int year,
//            @Param("month") int month);

//    @Query("SELECT (COUNT(CASE WHEN e.completionStatus = 'COMPLETED' THEN 1 END) * 100.0) / COUNT(e) " +
//            "FROM Enrollment e " +
//            "WHERE e.orderedCourses.course.id IN :courseIds " +
//            "AND YEAR(e.createdAt) = :year " +
//            "AND MONTH(e.createdAt) = :month")
//    double calculateCompletionRate(
//            @Param("courseIds") List<Long> courseIds,
//            @Param("year") int year,
//            @Param("month") int month);

    @Query("SELECT new com.sesac.backend.enrollments.dto.response.RecentEnrollmentDto(" +
            "e.user.id, " +
            "e.user.nickname, " +
            "oc.course.title, " +
            "e.createdAt) " +
            "FROM Enrollment e " +
            "JOIN e.orderedCourses oc " +
            "WHERE oc.course.id = :courseId " +
            "AND e.isActive = true " +
            "AND oc.status = 'ACTIVE' " +
            "ORDER BY e.createdAt DESC")
    List<RecentEnrollmentDto> findEnrolledUsersWithDateByCourseId(@Param("courseId") Long courseId);
}
