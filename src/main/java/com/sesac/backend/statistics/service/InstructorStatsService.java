package com.sesac.backend.statistics.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.enrollments.domain.Enrollment;
import com.sesac.backend.enrollments.repository.EnrollmentRepository;
import com.sesac.backend.orders.domain.OrderedCourses;
import com.sesac.backend.orders.repository.OrderedCoursesRepository;
import com.sesac.backend.reviews.repository.ReviewRepository;
import com.sesac.backend.statistics.domain.InstructorStats;
import com.sesac.backend.statistics.dto.*;
import com.sesac.backend.statistics.repository.InstructorStatsRepository;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.enums.UserType;
import com.sesac.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        List<MonthlyRevenueDto> monthlyRevenue = getLast12MonthRevenue(user);

        // 현재 월의 통계 데이터 조회
        LocalDateTime now = LocalDateTime.now();
        MonthlyStatsData currentMonth = stats.getMonthlyStats(now.getYear(), now.getMonth().getValue());
        if (currentMonth == null) {
            currentMonth = MonthlyStatsData.builder()
                    .revenue(BigDecimal.ZERO)
                    .newStudents(0)
                    .averageRating(0.0)
                    .build();
        }

        // 지난 달 통계
        MonthlyStatsData lastMonth = stats.getMonthlyStats(
                now.minusMonths(1).getYear(),
                now.minusMonths(1).getMonthValue()
        );
        if (lastMonth == null) {
            lastMonth = MonthlyStatsData.builder()
                    .revenue(BigDecimal.ZERO)
                    .newStudents(0)
                    .averageRating(0.0)
                    .build();
        }

        // 각 지표별 증감률 계산
        StatsTrendDto studentsTrend = calculateTrend(
                currentMonth.getNewStudents(),
                lastMonth.getNewStudents()
        );

        log.info("Current month students: {}", currentMonth.getNewStudents());
        log.info("Last month students: {}", lastMonth.getNewStudents());

        StatsTrendDto revenueTrend = calculateTrend(
                currentMonth.getRevenue(),
                lastMonth.getRevenue()
        );

        StatsTrendDto ratingTrend = calculateRatingTrend(
                currentMonth.getAverageRating(),
                lastMonth.getAverageRating()
        );

        // 현재월과 이전월 데이터를 상세 정보로 추가
        Map<String, Object> studentsDetail = new HashMap<>();
        studentsDetail.put("total", stats.getTotalStudents());
        studentsDetail.put("currentMonth", currentMonth.getNewStudents());
        studentsDetail.put("previousMonth", lastMonth.getNewStudents());
        studentsDetail.put("trend", studentsTrend);
        studentsDetail.put("currentMonthLabel", String.format("%d년 %d월", now.getYear(), now.getMonthValue()));
        studentsDetail.put("previousMonthLabel", String.format("%d년 %d월", now.minusMonths(1).getYear(), now.minusMonths(1).getMonthValue()));

        Map<String, Object> revenueDetail = new HashMap<>();
        revenueDetail.put("total", stats.getTotalRevenue());
        revenueDetail.put("currentMonth", currentMonth.getRevenue());
        revenueDetail.put("previousMonth", lastMonth.getRevenue());
        revenueDetail.put("trend", revenueTrend);
        revenueDetail.put("currentMonthLabel", String.format("%d년 %d월", now.getYear(), now.getMonthValue()));
        revenueDetail.put("previousMonthLabel", String.format("%d년 %d월", now.minusMonths(1).getYear(), now.minusMonths(1).getMonthValue()));

        instructorStats.put("totalStudents", stats.getTotalStudents());
        instructorStats.put("activeCourses", stats.getActiveCourses());
        instructorStats.put("totalRevenue", stats.getTotalRevenue());
        instructorStats.put("averageRating", stats.getAverageRating());
        instructorStats.put("monthlyStats", currentMonth);
        instructorStats.put("monthlyRevenue", monthlyRevenue);
        instructorStats.put("totalStudentsTrend", studentsTrend);
        instructorStats.put("monthlyRevenueTrend", revenueTrend);
        instructorStats.put("averageRatingTrend", ratingTrend);
        instructorStats.put("studentsDetail", studentsDetail);
        instructorStats.put("revenueDetail", revenueDetail);


        log.info("Instructor stats final: " + instructorStats);

        return instructorStats;
    }

    @Transactional
    public void updateInstructorStats() {
        List<User> instructors = userRepository.findAllByUserType(UserType.INSTRUCTOR);
        for (User instructor : instructors) {
            updateInstructorStats(instructor);
        }
    }

    @Transactional
    public void updateInstructorStats(User instructor) {

        InstructorStats stats = getOrCreateInstructorStats(instructor);
        CourseIdsDto ids = getCourseAndOrderedCourseIds(instructor);

        int totalEnrolled = enrollmentRepository.countByOrderedCoursesIdIn(ids.getSortedCourseIds());
        BigDecimal totalRevenue = orderedCoursesRepository.sumPriceByIds(ids.getSortedCourseIds());
        Double averageRating = reviewRepository.averageRatingByCourseIds(ids.getCourseIds());

        stats.setTotalStudents(totalEnrolled);
        stats.setActiveCourses(ids.getCourses().size());
        stats.setTotalRevenue(totalRevenue);
        stats.setAverageRating(averageRating);

        instructorStatsRepository.save(stats);
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
        log.info("getOrCreateInstructorStats entrance");
        return instructorStatsRepository.findByUserId(user.getId())
                .orElseGet(() -> createInitialStats(user));
    }

    private InstructorStats createInitialStats(User user) {

        log.info("createInitialStats entrance*****************");

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

        List<Long> distinctCourseIds = new ArrayList<>(new LinkedHashSet<>(sortedCourseIds));

        log.info("sortedCourseIds*******************: " + sortedCourseIds);

        return CourseIdsDto.builder()
                .courses(courses)
                .courseIds(courseIds)
                .sortedCourseIds(sortedCourseIds)
                .distinctCourseIds(distinctCourseIds)
                .build();
    }

    public List<MonthlyRevenueDto> getLast12MonthRevenue(User user) {
        List<MonthlyRevenueDto> revenueData = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        CourseIdsDto ids = getCourseAndOrderedCourseIds(user);

        for (int i = 11; i >= 0; i--) {
            LocalDateTime targetDate = now.minusMonths(i);
            BigDecimal monthlyRevenue = calculateMonthlyRevenue(
                    ids.getSortedCourseIds(),
                    targetDate.getYear(),
                    targetDate.getMonthValue()
            );

            revenueData.add(MonthlyRevenueDto.builder()
                            .yearMonth(String.format("%d-%02d", targetDate.getYear(), targetDate.getMonthValue()))
                            .revenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO)
                    .build());
        }

        return revenueData;
    }

    // BigDecimal용
    private StatsTrendDto calculateTrend(BigDecimal currentValue, BigDecimal previousValue) {
        currentValue = currentValue != null ? currentValue : BigDecimal.ZERO;
        previousValue = previousValue != null ? previousValue : BigDecimal.ZERO;

        boolean isNew = previousValue.compareTo(BigDecimal.ZERO) == 0 && currentValue.compareTo(BigDecimal.ZERO) > 0;
        double trend;
        String trendType;

        if (isNew) {
            trend = 0.0;
            trendType = "NEW";
        } else if (previousValue.compareTo(BigDecimal.ZERO) > 0) {
            trend = currentValue.subtract(previousValue)
                    .multiply(new BigDecimal("100"))
                    .divide(previousValue, 2, RoundingMode.HALF_UP)
                    .doubleValue();
            trendType = trend > 0 ? "INCREASE" : trend < 0 ? "DECREASE" : "NO_CHANGE";
        } else {
            trend = 0.0;
            trendType = "NO_CHANGE";
        }

        return StatsTrendDto.builder()
                .value(currentValue.intValue())
                .trend(trend)
                .trendType(trendType)
                .isNew(isNew)
                .build();
    }

    // 일반 숫자용
    private StatsTrendDto calculateTrend(int currentValue, int previousValue) {
        log.info("Calculating trend - current value: {}, previous value: {}", currentValue, previousValue);
        boolean isNew = previousValue == 0 && currentValue > 0;
        double trend;
        String trendType;

        if (isNew) {
            trend = 0.0;
            trendType = "NEW";
        } else if (previousValue > 0) {
            trend = ((double)(currentValue - previousValue) / previousValue) * 100;
            log.info("Calculated trend: {}", trend);
            trendType = trend > 0 ? "INCREASE" : trend < 0 ? "DECREASE" : "NO_CHANGE";
        } else {
            trend = 0.0;
            trendType = "NO_CHANGE";
        }

        log.info("Final trend result - value: {}, trend: {}, type: {}, isNew: {}",
                currentValue, trend, trendType, isNew);

        return StatsTrendDto.builder()
                .value(currentValue)
                .trend(trend)
                .trendType(trendType)
                .isNew(isNew)
                .build();
    }

    // 평점용 calculateRatingTrend 메소드
    private StatsTrendDto calculateRatingTrend(Double currentValue, Double previousValue) {
        double trend = 0.0;
        String trendType = "NO_CHANGE";

        currentValue = currentValue != null ? currentValue : 0.0;
        previousValue = previousValue != null ? previousValue : 0.0;

        if (previousValue == 0 && currentValue > 0) {
            trend = currentValue;  // 평점은 차이값 자체를 사용
            trendType = "INCREASE";
        }
        else if (previousValue > 0) {
            trend = currentValue - previousValue;
            trendType = trend > 0 ? "INCREASE" : trend < 0 ? "DECREASE" : "NO_CHANGE";
        }

        return StatsTrendDto.builder()
                .value((int)(currentValue * 10))
                .trend(Math.round(trend * 10.0) / 10.0)
                .trendType(trendType)
                .build();
    }


}
