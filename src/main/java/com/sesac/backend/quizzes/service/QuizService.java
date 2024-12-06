package com.sesac.backend.quizzes.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.enrollments.domain.Enrollment;
import com.sesac.backend.enrollments.repository.EnrollmentRepository;
import com.sesac.backend.quizProblems.domain.QuizProblem;
import com.sesac.backend.quizProblems.dto.request.QuizProblemCreationDto;
import com.sesac.backend.quizzes.domain.Quiz;
import com.sesac.backend.quizzes.dto.request.QuizCreationRequest;
import com.sesac.backend.quizzes.repository.QuizRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public Long createQuiz(QuizCreationRequest request) {
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(RuntimeException::new);

        enrollmentRepository.findAllByCourse(course).stream().map(Enrollment::getStudent).forEach(
            student -> {

            }
        );

        return
    }

    private QuizProblem convertToQuizProblem(QuizProblemCreationDto dto, Quiz quiz) {
        return QuizProblem.builder().quiz(quiz).problemNumber(dto.getProblemNumber())
            .correctAnswer(dto.getCorrectAnswer()).question(dto.getQuestion())
            .choices(dto.getChoices()).build();
    }

    private Quiz convertToQuiz(QuizCreationRequest request) {
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(RuntimeException::new);

        return Quiz.builder().quizNumber(request.getQuizNumber()).course(course)
    }
}
