package com.sesac.backend.enrollments.repository;

import com.sesac.backend.enrollments.domain.Enrollment;
import com.sesac.backend.orders.domain.OrderedCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByOrderedCourses(OrderedCourses orderedcourses);

    boolean existsByOrderedCourses(OrderedCourses orderedcourses);

}
