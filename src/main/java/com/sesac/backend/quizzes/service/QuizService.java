package com.sesac.backend.quizzes.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.enrollments.domain.Enrollment;
import com.sesac.backend.enrollments.repository.EnrollmentRepository;
import com.sesac.backend.quizProblems.domain.QuizProblem;
import com.sesac.backend.quizProblems.dto.request.QuizProblemAnswerDto;
import com.sesac.backend.quizProblems.dto.request.QuizProblemCreationDto;
import com.sesac.backend.quizProblems.dto.response.QuizProblemDetailDto;
import com.sesac.backend.quizProblems.dto.response.QuizProblemResultDto;
import com.sesac.backend.quizProblems.enums.Answer;
import com.sesac.backend.quizProblems.enums.Correctness;
import com.sesac.backend.quizzes.domain.Quiz;
import com.sesac.backend.quizzes.dto.request.QuizCreationRequest;
import com.sesac.backend.quizzes.dto.request.QuizSubmissionRequest;
import com.sesac.backend.quizzes.dto.response.QuizCreationResponse;
import com.sesac.backend.quizzes.dto.response.QuizDetailResponse;
import com.sesac.backend.quizzes.dto.response.QuizReadResponse;
import com.sesac.backend.quizzes.dto.response.QuizResultResponse;
import com.sesac.backend.quizzes.dto.response.QuizSubmissionResponse;
import com.sesac.backend.quizzes.repository.QuizRepository;
import com.sesac.backend.users.domain.User;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
    public QuizCreationResponse createQuiz(QuizCreationRequest request, UUID userId) {
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(RuntimeException::new);

        if (!course.getInstructor().getUuid().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        List<Quiz> quizzes = enrollmentRepository.findAllByOrderedCoursesCourse(course)
            .stream().map(Enrollment::getUser)
            .map(student -> {
                    Quiz quiz = creationRequestToQuiz(request, course, student);
                    List<QuizProblem> problems = request.getQuizProblems().stream()
                        .map(dto -> creationDtoToProblem(dto, quiz))
                        .sorted(Comparator.comparingInt(QuizProblem::getProblemNumber))
                        .toList();
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

    /**
     * 강좌에서 내 퀴즈 목록 조회
     *
     * @param courseId
     * @param userId
     * @return List<QuizReadResponse>
     */
    public List<QuizReadResponse> findMyQuizzes(Long courseId, UUID userId) {
        Course course = courseRepository.findById(courseId).orElseThrow(RuntimeException::new);

        return quizRepository.findAllByStudentUuidAndCourseId(userId, courseId)
            .stream().map(entity -> quizToReadResponse(entity, course)).toList();
    }

    private QuizReadResponse quizToReadResponse(Quiz quiz, Course course) {
        return QuizReadResponse.builder().id(quiz.getId()).title(generateQuizTitle(quiz, course))
            .build();
    }

    private String generateQuizTitle(Quiz quiz, Course course) {
        return course.getTitle() + " 퀴즈" + quiz.getQuizNumber();
    }

    /**
     * 퀴즈 상세 조회
     *
     * @param quizId
     * @param userId
     * @return QuizDetailResponse
     */
    public QuizDetailResponse findQuizDetail(Long quizId, UUID userId) {
        Quiz quiz = quizRepository.findQuizWithDetails(quizId).orElseThrow(RuntimeException::new);

        validateAccess(quiz, userId);
        validateQuizTime(quiz, userId);

        return quizToDetailResponse(quiz);
    }

    private QuizDetailResponse quizToDetailResponse(Quiz quiz) {
        List<QuizProblemDetailDto> problemDetailDtos = quiz.getQuizProblems().stream()
            .map(this::problemToDetailDto)
            .toList();

        return QuizDetailResponse.builder()
            .quizId(quiz.getId())
            .title(generateQuizTitle(quiz))
            .duration(getDuration(quiz))
            .startTime(quiz.getStartTime())
            .endTime(quiz.getEndTime())
            .totalQuestions(problemDetailDtos.size())
            .problems(problemDetailDtos)
            .build();
    }

    private String generateQuizTitle(Quiz quiz) {
        return quiz.getCourse().getTitle() + " 퀴즈" + quiz.getQuizNumber();
    }

    private String getDuration(Quiz quiz) {
        return String.valueOf(ChronoUnit.MINUTES.between(quiz.getStartTime(), quiz.getEndTime()));
    }

    private QuizProblemDetailDto problemToDetailDto(QuizProblem quizProblem) {
        return QuizProblemDetailDto.builder()
            .problemId(quizProblem.getId())
            .number(quizProblem.getProblemNumber())
            .question(quizProblem.getQuestion())
            .options(quizProblem.getChoices())
            .build();
    }

    private void validateAccess(Quiz quiz, UUID userId) {
        boolean isStudent = quiz.getStudent().getUuid().equals(userId);
        boolean isInstructor = quiz.getCourse().getInstructor().getUuid().equals(userId);

        if (!isStudent && !isInstructor) {
            throw new RuntimeException("권한이 없습니다.");
        }
    }

    private void validateQuizTime(Quiz quiz, UUID userId) {
        if (quiz.getStudent().getUuid().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            boolean isBeforeQuiz = now.isBefore(quiz.getStartTime());
            boolean isAfterQuiz = now.isAfter(quiz.getEndTime());

            if (isBeforeQuiz || isAfterQuiz) {
                throw new RuntimeException("시험 시간이 아닙니다. " +
                    "시험 시간: " + quiz.getStartTime() + " ~ " + quiz.getEndTime());
            }
        }
    }

    /**
     * 퀴즈 삭제
     *
     * @param quizId
     * @param userId
     */
    public void deleteQuiz(Long quizId, UUID userId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(RuntimeException::new);

        if (!quiz.getCourse().getInstructor().getUuid().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        quizRepository.delete(quiz);
    }

    /**
     * 퀴즈 제출 및 채점
     *
     * @param request
     * @param userId
     * @return QuizSubmissionResponse
     */
    public QuizSubmissionResponse submitQuiz(QuizSubmissionRequest request, UUID userId) {
        Quiz quiz = quizRepository.findById(request.getQuizId()).orElseThrow(RuntimeException::new);

        if (!quiz.getStudent().getUuid().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(quiz.getStartTime()) || now.isAfter(quiz.getEndTime())) {
            throw new RuntimeException("제출 기한이 아닙니다.");
        }

        List<QuizProblem> problems = quiz.getQuizProblems().stream()
            .sorted(Comparator.comparingInt(QuizProblem::getProblemNumber)).toList();
        List<QuizProblemAnswerDto> answers = getAnswers(request, problems);

        Integer score = IntStream.range(0, problems.size())
            .map(idx -> {
                QuizProblem problem = problems.get(idx);
                QuizProblemAnswerDto answer = answers.get(idx);

                problem.setSelectedAnswer(answer.getSelectedAnswer());

                if (problem.getCorrectAnswer() == answer.getSelectedAnswer()) {
                    problem.setCorrectness(Correctness.CORRECT);
                    return problem.getDifficulty().getPoint();
                }

                problem.setCorrectness(Correctness.WRONG);
                return 0;
            }).sum();

        quiz.setQuizProblems(problems);
        quiz.setScore(score);

        quizRepository.save(quiz);

        return QuizSubmissionResponse.builder().quizId(quiz.getId()).score(score).build();
    }

    private List<QuizProblemAnswerDto> getAnswers(QuizSubmissionRequest request,
        List<QuizProblem> problems) {
        Map<Integer, QuizProblemAnswerDto> answers = request.getAnswers().stream().collect(
            Collectors.toMap(QuizProblemAnswerDto::getProblemNumber, answer -> answer));

        return problems.stream()
            .map(problem -> answers.getOrDefault(
                problem.getProblemNumber(),
                QuizProblemAnswerDto.builder()
                    .problemId(problem.getId())
                    .problemNumber(problem.getProblemNumber())
                    .selectedAnswer(Answer.NOT_SELECTED)
                    .build()
            ))
            .sorted(Comparator.comparingInt(QuizProblemAnswerDto::getProblemNumber))
            .toList();
    }

    /**
     * 퀴즈 결과 조회
     *
     * @param quizId
     * @param userId
     * @return
     */
    public QuizResultResponse findQuizResult(Long quizId, UUID userId) {
        Quiz quiz = quizRepository.findQuizWithResult(quizId).orElseThrow(RuntimeException::new);

        if (!quiz.getStudent().getUuid().equals(userId)
            && !quiz.getCourse().getInstructor().getUuid().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        return quizToResultResponse(quiz);
    }

    private QuizResultResponse quizToResultResponse(Quiz quiz) {
        return QuizResultResponse.builder()
            .quizId(quiz.getId())
            .title(generateQuizTitle(quiz))
            .startTime(quiz.getStartTime())
            .endTime(quiz.getEndTime())
            .score(quiz.getScore())
            .problemResults(quiz.getQuizProblems().stream().map(this::problemToResultDto).sorted(
                Comparator.comparingInt(QuizProblemResultDto::getNumber)).toList())
            .build();
    }

    private QuizProblemResultDto problemToResultDto(QuizProblem quizProblem) {
        return QuizProblemResultDto.builder()
            .problemId(quizProblem.getId())
            .number(quizProblem.getProblemNumber())
            .correctness(quizProblem.getCorrectness())
            .difficulty(quizProblem.getDifficulty().getPoint())
            .correctAnswer(quizProblem.getCorrectAnswer())
            .selectedAnswer(quizProblem.getSelectedAnswer())
            .question(quizProblem.getQuestion())
            .choices(quizProblem.getChoices())
            .build();
    }
}
