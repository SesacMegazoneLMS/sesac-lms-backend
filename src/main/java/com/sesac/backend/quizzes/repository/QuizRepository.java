package com.sesac.backend.quizzes.repository;

import com.sesac.backend.quizzes.domain.Quiz;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByStudentUserIdAndCourseId(UUID studentUserId, Long courseId);

    @Query("""
    SELECT DISTINCT q FROM Quiz q
    JOIN FETCH q.student s
    JOIN FETCH q.course c
    JOIN FETCH c.instructor i
    JOIN FETCH q.quizProblems qp
    JOIN FETCH qp.choices
    WHERE q.id = :quizId
    """)
    Optional<Quiz> findQuizWithDetails(Long quizId);

    @Query("SELECT q FROM Quiz q " +
        "JOIN FETCH q.student " +
        "JOIN FETCH q.course c " +
        "JOIN FETCH c.instructor " +
        "JOIN FETCH q.quizProblems " +
        "WHERE q.id = :quizId")
    Optional<Quiz> findQuizWithResult(Long quizId);
}
