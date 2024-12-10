package com.sesac.backend.courses.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.dto.CourseDto;
import com.sesac.backend.courses.enums.Category;
import com.sesac.backend.courses.enums.Level;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    public void createCourse(UUID userId, CourseDto request) {

        User user = userRepository.findByUuid(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다"));

        System.out.println("찾은 유저 정보 : " + user);

        Course course = Course.builder()
                .instructor(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .level(Level.from(request.getLevel()))
                .category(Category.valueOf(request.getCategory().toUpperCase()))
                .price(request.getPrice())
                .thumbnail(request.getThumbnail())
                .objectives(request.getObjectives())
                .requirements(request.getRequirements())
                .skills(request.getSkills())
                .lectures(request.getLectures())
                .build();

        System.out.println("생성할 course 정보 : " + course);

        courseRepository.save(course);
    }

    public Set<CourseDto> getAllCourses() {

        List<Course> courses = courseRepository.findAll();

        Set<CourseDto> courseDtos = new HashSet<>();

        for (Course course : courses) {
            courseDtos.add(CourseDto.builder()
                    .id(course.getId())
                    .title(course.getTitle())
                    .description(course.getDescription())
                    .level(course.getLevel().getLevel())
                    .category(course.getCategory().toString())
                    .price(course.getPrice())
                    .thumbnail(course.getThumbnail())
                    .objectives(course.getObjectives())
                    .requirements(course.getRequirements())
                    .skills(course.getSkills())
                    .lectures(course.getLectures())
                    .build());
        }

        return courseDtos;
    }

    public CourseDto getCourseByCourseId(Long id) {

        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("강의가 존재하지 않습니다"));

        return CourseDto.builder()
                .title(course.getTitle())
                .description(course.getDescription())
                .level(course.getLevel().getLevel())
                .category(course.getCategory().toString())
                .price(course.getPrice())
                .thumbnail(course.getThumbnail())
                .objectives(course.getObjectives())
                .requirements(course.getRequirements())
                .skills(course.getSkills())
                .lectures(course.getLectures())
                .build();
    }

    public void updateCourse(Long courseId, UUID userId, CourseDto request) {

        User user = userRepository.findByUuid(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다"));

        Course course = courseRepository.findByInstructorAndId(user, courseId).orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다"));

        course.setTitle(course.getTitle());
        course.setDescription(course.getDescription());
        course.setLevel(Level.from(request.getLevel()));
        course.setCategory(Category.valueOf(request.getCategory()));
        course.setPrice(request.getPrice());
        course.setThumbnail(request.getThumbnail());
        course.setObjectives(request.getObjectives());
        course.setRequirements(request.getRequirements());
        course.setSkills(request.getSkills());
        course.setLectures(request.getLectures());

        courseRepository.save(course);
    }

    public void deleteCourse(Long courseId, UUID userId) {

        User user = userRepository.findByUuid(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다"));

        courseRepository.deleteByInstructorAndId(user, courseId);
    }
}
