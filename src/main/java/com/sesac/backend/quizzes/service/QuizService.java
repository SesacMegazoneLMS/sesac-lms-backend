package com.sesac.backend.quizzes.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.enrollments.domain.Enrollment;
import com.sesac.backend.enrollments.repository.EnrollmentRepository;
import com.sesac.backend.quizProblems.domain.QuizProblem;
import com.sesac.backend.quizProblems.dto.request.QuizProblemCreationDto;
import com.sesac.backend.quizzes.domain.Quiz;
import com.sesac.backend.quizzes.dto.request.QuizCreationRequest;
import com.sesac.backend.quizzes.dto.response.QuizCreationResponse;
import com.sesac.backend.quizzes.repository.QuizRepository;
import com.sesac.backend.users.domain.User;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * 퀴즈 생성
     *
     * @param request
     * @return QuizCreationResponse
     */
    public QuizCreationResponse createQuiz(QuizCreationRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(RuntimeException::new);

        List<Quiz> quizzes = enrollmentRepository.findAllByOrderedCoursesCourse(course)
            .stream().map(Enrollment::getUser)
            .map(student -> {
                    Quiz quiz = creationRequestToQuiz(request, course, student);
                    List<QuizProblem> problems = request.getQuizProblems().stream()
                        .map(dto -> creationDtoToProblem(dto, quiz)).toList();
                    quiz.setQuizProblems(problems);
                    return quiz;
                }
            ).toList();

        quizRepository.saveAll(quizzes);

        return new QuizCreationResponse(request.getQuizNumber(), request.getCourseId(),
            request.getStartTime(), request.getEndTime());
    }

    private QuizProblem creationDtoToProblem(QuizProblemCreationDto dto, Quiz quiz) {
        return QuizProblem.builder().quiz(quiz).problemNumber(dto.getProblemNumber())
            .correctAnswer(dto.getCorrectAnswer()).question(dto.getQuestion())
            .choices(dto.getChoices()).build();
    }

    private Quiz creationRequestToQuiz(QuizCreationRequest request, Course course, User student) {
        return Quiz.builder().quizNumber(request.getQuizNumber()).course(course).student(student)
            .startTime(request.getStartTime()).endTime(request.getEndTime()).build();
    }
}
