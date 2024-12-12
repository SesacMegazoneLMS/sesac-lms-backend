package com.sesac.backend.courses.repository;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.users.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByInstructorAndId(User user, Long courseId);

    //Optional<Course> deleteByInstructorAndCourseId(User user, Long courseId);
    void deleteByInstructorAndId(User user, Long courseId);

    Page<Course> findByInstructor(User user, Pageable pageable);
}
