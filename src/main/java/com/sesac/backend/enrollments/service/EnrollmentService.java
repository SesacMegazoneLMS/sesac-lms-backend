package com.sesac.backend.enrollments.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.dto.CourseDto;
import com.sesac.backend.enrollments.domain.Enrollment;
import com.sesac.backend.enrollments.dto.response.EnrolledLectureDto;
import com.sesac.backend.enrollments.dto.response.EnrollmentResponse;
import com.sesac.backend.enrollments.repository.EnrollmentRepository;
import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.lectures.dto.request.LectureRequest;
import com.sesac.backend.orders.constants.OrderedCoursesStatus;
import com.sesac.backend.orders.domain.OrderedCourses;
import com.sesac.backend.orders.repository.OrderedCoursesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public List<EnrollmentResponse> getEnrollmentsByUserUuid(UUID userId) {

        log.info("💰UUID userId: " + userId);

        List<Enrollment> enrollments = enrollmentRepository
                .findActiveEnrollmentsWithCoursesByUserUuid(userId, OrderedCoursesStatus.ACTIVE);

        log.info("💰enrollments: " + enrollments);

        return enrollments.stream()
                .map(this::convertToEnrolledCourseDto)
                .collect(Collectors.toList());
    }

    private EnrollmentResponse convertToEnrolledCourseDto(Enrollment enrollment) {
        OrderedCourses orderedCourse = enrollment.getOrderedCourses();
        Course course = orderedCourse.getCourse();

        // LocalDateTime을 String으로 변환
        String enrolledAtStr = enrollment.getCreatedAt()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return EnrollmentResponse.builder()
                .courseId(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .thumbnail(course.getThumbnail())
                .level(course.getLevel().name())
                .category(course.getCategory().name())
                .objectives(course.getObjectives())
                .price(orderedCourse.getPrice())
                .progress(0) // TODO: 진도율 계산 로직 추가
                .enrolledAt(enrolledAtStr)
                .lectures(course.getLectures().stream()
                        .sorted(Comparator.comparing(Lecture::getOrderIndex))
                        .map(this::convertToEnrolledLectureDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private EnrolledLectureDto convertToEnrolledLectureDto(Lecture lecture) {
        return EnrolledLectureDto.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .duration(lecture.getDuration())
                .orderIndex(lecture.getOrderIndex())
                .videoKey(lecture.getVideoKey())
                .cloudFrontUrl(lecture.getCloudFrontUrl())
                .status(lecture.getStatus())
                .build();
    }
}

