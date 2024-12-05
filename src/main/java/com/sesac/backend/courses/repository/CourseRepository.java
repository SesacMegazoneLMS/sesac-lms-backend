package com.sesac.backend.courses.repository;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByUserAndCourseId(User user, Long courseId);

    Optional<Course> deleteByUserAndCourseId(User user, Long courseId);
}
