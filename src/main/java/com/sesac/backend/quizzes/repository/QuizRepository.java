package com.sesac.backend.quizzes.repository;

import com.sesac.backend.quizzes.domain.Quiz;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByStudentUserIdAndCourseId(UUID studentUserId, Long courseId);
}
