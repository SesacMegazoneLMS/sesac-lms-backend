package com.sesac.backend.courses.repository;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.users.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    Optional<Course> findByInstructorAndId(User user, Long courseId);

    //Optional<Course> deleteByInstructorAndCourseId(User user, Long courseId);
    void deleteByInstructorAndId(User user, Long courseId);

    Page<Course> findByInstructor(User user, Pageable pageable);

    List<Course> findByInstructor(User user);

    @Query("SELECT c.id FROM Course c WHERE c.instructor = :instructor")
    List<Long> findCourseIdsByInstructor(@Param("instructor") User user);

    Optional<Course> findTitleById(Long courseId);

}
