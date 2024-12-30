package com.sesac.backend.statistics.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.enrollments.domain.Enrollment;
import com.sesac.backend.enrollments.repository.EnrollmentRepository;
import com.sesac.backend.orders.domain.OrderedCourses;
import com.sesac.backend.orders.repository.OrderedCoursesRepository;
import com.sesac.backend.reviews.repository.ReviewRepository;
import com.sesac.backend.statistics.domain.InstructorStats;
import com.sesac.backend.statistics.dto.InstructorStatsDto;
import com.sesac.backend.statistics.repository.InstructorStatsRepository;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
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

        Long instructorId = user.getId();

        List<Course> courses = courseRepository.findByInstructorId(instructorId);
        List<Long> courseIds = courseRepository.findCourseIdsByInstructorId(instructorId);

        List<List<OrderedCourses>> allOrderedCourses = new ArrayList<>();
        List<Long> orderedCourseIds = new ArrayList<>();

        for (Course course : courses) {
            allOrderedCourses.add(orderedCoursesRepository.findAllByCourseId(course.getId()));
        }

        for (List<OrderedCourses> orderedCourses : allOrderedCourses) {
            if (enrollmentRepository.existsByOrderedCoursesId(orderedCourses.get(0).getId())) {
                orderedCourseIds.add(orderedCourses.get(0).getId());
            }
        }

        Integer totalEnrolled = enrollmentRepository.countByOrderedCoursesIdIn(orderedCourseIds);
        BigDecimal totalRevenue = orderedCoursesRepository.sumPriceByIds(orderedCourseIds);
        Double averageRating = reviewRepository.averageRatingByCourseIds(courseIds);

        InstructorStats stats = InstructorStats.builder()
                .user(user)
                .totalStudents(totalEnrolled)
                .activeCourses(courses.size())
                .totalRevenue(totalRevenue)
                .averageRating(averageRating)
                .build();

        instructorStatsRepository.save(stats);

        instructorStats.put("totalStudents", totalEnrolled);
        instructorStats.put("activeCourses", courses.size());
        instructorStats.put("totalRevenue", totalRevenue);
        instructorStats.put("averageRating", averageRating);

        return instructorStats;
    }

}
