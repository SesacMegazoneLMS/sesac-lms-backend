package com.sesac.backend.enrollments.repository;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.enrollments.domain.Enrollment;
import com.sesac.backend.orders.constants.OrderedCoursesStatus;
import com.sesac.backend.orders.domain.OrderedCourses;
import java.util.List;
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

}
