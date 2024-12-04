package com.sesac.backend.courses.repository;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByUserAndId(User user, Long id);

    Optional<Course> deleteByUserAndId(User user, Long id);
}
