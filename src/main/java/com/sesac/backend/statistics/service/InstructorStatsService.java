package com.sesac.backend.statistics.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.enrollments.domain.Enrollment;
import com.sesac.backend.enrollments.repository.EnrollmentRepository;
import com.sesac.backend.orders.domain.OrderedCourses;
import com.sesac.backend.orders.repository.OrderedCoursesRepository;
import com.sesac.backend.reviews.repository.ReviewRepository;
import com.sesac.backend.statistics.domain.InstructorStats;
import com.sesac.backend.statistics.dto.CourseIdsDto;
import com.sesac.backend.statistics.dto.InstructorStatsDto;
import com.sesac.backend.statistics.dto.MonthlyStatsData;
import com.sesac.backend.statistics.repository.InstructorStatsRepository;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.enums.UserType;
import com.sesac.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstructorStatsService {

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    private final OrderedCoursesRepository orderedCoursesRepository;

    private final EnrollmentRepository enrollmentRepository;

    private final InstructorStatsRepository instructorStatsRepository;

    private final ReviewRepository reviewRepository;

    public Map<String, Object> getInstructorStats(UUID userId) {

        Map<String, Object> instructorStats = new HashMap<>();

        User user = userRepository.findByUuid(userId).orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User: " + user);

        InstructorStats stats = getOrCreateInstructorStats(user);

        log.info("Instructor stats: " + stats);

        // 현재 월의 통계 데이터 조회
        LocalDateTime now = LocalDateTime.now();
        MonthlyStatsData monthlyData = stats.getMonthlyStats(now.getYear(), now.getMonth().getValue());
        if (monthlyData == null) {
            monthlyData = MonthlyStatsData.builder()
                    .revenue(BigDecimal.ZERO)
                    .newStudents(0)
                    .averageRating(0.0)
                    .build();
        }

        instructorStats.put("totalStudents", stats.getTotalStudents());
        instructorStats.put("activeCourses", stats.getActiveCourses());
        instructorStats.put("totalRevenue", stats.getTotalRevenue());
        instructorStats.put("averageRating", stats.getAverageRating());
        instructorStats.put("monthlyStats", monthlyData);

        log.info("Instructor stats final: " + instructorStats);

        return instructorStats;
    }

    @Transactional
    public void updateAllInstructorsMonthlyStats() {

        log.info("updateAllInstructorsMonthlyStats entrance");

        List<User> instructors = userRepository.findAllByUserType(UserType.INSTRUCTOR);
        LocalDateTime now = LocalDateTime.now();

        log.info("instructor: " + instructors.get(0));

        for (User instructor : instructors) {
            updateInstructorMonthlyStats(instructor, now.getYear(), now.getMonthValue());
        }
    }

    private void updateInstructorMonthlyStats(User instructor, int year, int month) {

        try {
            log.info("updateInstructorMonthlyStats entrance");
            log.info("year: " + year);
            log.info("month: " + month);

            CourseIdsDto ids = getCourseAndOrderedCourseIds(instructor);

            log.info("updateInstructorMonthlyStats ids: " + ids.getSortedCourseIds());

            // 이번 달의 새로운 수강생 수 계산
            int newStudents = calculateNewStudentsForMonth(ids.getSortedCourseIds(), year, month);

            log.info("updateInstructorMonthlyStats newStudents: " + newStudents);

            // 이번 달의 수익 계산
            BigDecimal monthlyRevenue = calculateMonthlyRevenue(ids.getSortedCourseIds(), year, month);

            log.info("updateInstructorMonthlyStats revenue: " + monthlyRevenue);

//        // 이번 달의 수료율 계산
//        double completionRate = calculateCompletionRate(courseIds, year, month);

            // 이번 달의 평균 평점 계산
            Double averageRating = calculateAverageRating(ids.getCourseIds(), year, month);

            log.info("updateInstructorMonthlyStats averageRating: " + averageRating);

            MonthlyStatsData monthlyData = MonthlyStatsData.builder()
                    .revenue(monthlyRevenue)
                    .newStudents(newStudents)
//                .completionRate(completionRate)
                    .averageRating(averageRating)
                    .build();

            // 통계 정보 업데이트
            InstructorStats stats = getOrCreateInstructorStats(instructor);
            log.info("Retrieved stats object: {}", stats);

            stats.updateMonthlyStats(year, month, monthlyData);
            log.info("After updateMonthlyStats call");

            instructorStatsRepository.save(stats);
            log.info("After save operation");
        } catch (Exception e) {
            log.error("Error in updateInstructorMonthlyStats", e);
            throw new RuntimeException("통계 업데이트 중 오류 발생", e);
        }
    }

    private InstructorStats getOrCreateInstructorStats(User user) {
        return instructorStatsRepository.findByUserId(user.getId())
                .orElseGet(() -> createInitialStats(user));
    }

    private InstructorStats createInitialStats(User user) {

        CourseIdsDto ids = getCourseAndOrderedCourseIds(user);

        int totalEnrolled = enrollmentRepository.countByOrderedCoursesIdIn(ids.getSortedCourseIds());
        BigDecimal totalRevenue = orderedCoursesRepository.sumPriceByIds(ids.getSortedCourseIds());
        Double averageRating = reviewRepository.averageRatingByCourseIds(ids.getCourseIds());

        log.info("createInitialStats totalEnrolled: " + totalEnrolled);
        log.info("createInitialStats totalRevenue: " + totalRevenue);
        log.info("createInitialStats averageRating: " + averageRating);

        return InstructorStats.builder()
                .user(user)
                .totalStudents(totalEnrolled)
                .activeCourses(ids.getCourses().size())
                .totalRevenue(totalRevenue)
                .averageRating(averageRating)
                .build();
    }

    private Integer calculateNewStudentsForMonth(List<Long> courseIds, int year, int month) {
        // 해당 월의 새로운 수강생 수 계산 로직
        return orderedCoursesRepository.countNewEnrollmentsForMonth(courseIds, year, month);
    }

    private BigDecimal calculateMonthlyRevenue(List<Long> courseIds, int year, int month) {
        // 해당 월의 수익 계산 로직
        return orderedCoursesRepository.calculateMonthlyRevenue(courseIds, year, month);
    }

//    private double calculateCompletionRate(List<Long> courseIds, int year, int month) {
//        // 해당 월의 수료율 계산 로직
//        return enrollmentRepository.calculateCompletionRate(courseIds, year, month);
//    }

    private Double calculateAverageRating(List<Long> courseIds, int year, int month) {
        // 해당 월의 평균 평점 계산 로직
        return reviewRepository.averageRatingForMonth(courseIds, year, month);
    }

    public CourseIdsDto getCourseAndOrderedCourseIds(User user) {
        List<Course> courses = courseRepository.findByInstructor(user);
        List<Long> courseIds = courseRepository.findCourseIdsByInstructor(user);

        List<List<OrderedCourses>> allOrderedCourses = new ArrayList<>();
        List<Long> sortedCourseIds = new ArrayList<>();

        for (Course course : courses) {
            if (orderedCoursesRepository.existsByCourse(course)) {
                allOrderedCourses.add(orderedCoursesRepository.findAllByCourse(course));
            }
        }

        for (List<OrderedCourses> orderedCourses : allOrderedCourses) {
            for (OrderedCourses orderedCourse : orderedCourses) {
                if (enrollmentRepository.existsByOrderedCourses(orderedCourse)) {
                    sortedCourseIds.add(orderedCourse.getCourse().getId());
                }
            }
        }

        return CourseIdsDto.builder()
                .courses(courses)
                .courseIds(courseIds)
                .sortedCourseIds(sortedCourseIds)
                .build();
    }


}
