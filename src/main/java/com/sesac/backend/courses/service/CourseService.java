package com.sesac.backend.courses.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.dto.CourseDto;
import com.sesac.backend.courses.dto.CourseInstructorDto;
import com.sesac.backend.courses.dto.CourseSearchCriteria;
import com.sesac.backend.courses.enums.Category;
import com.sesac.backend.courses.enums.Level;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.lectures.dto.request.LectureRequest;
import com.sesac.backend.reviews.domain.Review;
import com.sesac.backend.reviews.repository.ReviewRepository;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public Long createCourse(UUID userId, CourseDto request) {

        User user = userRepository.findByUuid(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다"));

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
                .lectures(new ArrayList<>()) // 빈 리스트로 초기화
                .build();

        // Lecture 엔티티들 생성 및 연결
        if (request.getLectures() != null) {
            List<Lecture> lectures = request.getLectures().stream()
                    .map(lectureRequest -> lectureRequest.toEntity(course))
                    .toList();
            course.setLectures(lectures);
        }

        courseRepository.save(course);

        return course.getId();
    }

    public List<CourseDto> getAllCourses() {

        List<Course> courses = courseRepository.findAll();

        List<CourseDto> courseDtos = new ArrayList<>();

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
                    .lectures(course.getLectures().stream()
                            .map(LectureRequest::from)
                            .toList())
                    .build());
        }

        return courseDtos;
    }

    public CourseDto getCourseByCourseId(Long id) {

        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("강의가 존재하지 않습니다"));

        return CourseDto.builder()
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
                .lectures(course.getLectures().stream()
                        .map(LectureRequest::from)
                        .toList())
                .build();
    }

    public void updateCourse(Long courseId, UUID userId, CourseDto request) {

        User user = userRepository.findByUuid(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다"));

        Course course = courseRepository.findByInstructorAndId(user, courseId).orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다"));

        course.setTitle(request.getTitle());
        course.setDescription(course.getDescription());
        course.setLevel(Level.from(request.getLevel()));
        course.setCategory(Category.valueOf(request.getCategory()));
        course.setPrice(request.getPrice());
        course.setThumbnail(request.getThumbnail());
        course.setObjectives(request.getObjectives());
        course.setRequirements(request.getRequirements());
        course.setSkills(request.getSkills());

        if (request.getLectures() != null) {
            // 기존 강의 목록 초기화
            course.getLectures().clear();

            // 수정된 강의 목록 추가
            List<Lecture> lectures = request.getLectures().stream()
                    .map(lectureRequest -> lectureRequest.toEntity(course))
                    .toList();
            course.setLectures(lectures);
        }

        courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public Page<CourseDto> searchCourses(CourseSearchCriteria criteria, PageRequest pageRequest) {

        Sort sort = createSort(criteria.getSort());
        PageRequest sortedPageRequest = PageRequest.of(
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                sort
        );

        // 검색 조건에 따른 쿼리 생성
        Specification<Course> spec = Specification.where(null);

        if (criteria.getCategory() != null && !criteria.getCategory().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category"), Category.fromValue(criteria.getCategory())));
        }

        if (criteria.getLevel() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("level"), Level.from(criteria.getLevel())));
        }

        if (criteria.getSearch() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(root.get("title"), "%" + criteria.getSearch() + "%"),
                            cb.like(root.get("description"), "%" + criteria.getSearch() + "%")
                    ));
        }

        Page<Course> coursePage = courseRepository.findAll(spec, sortedPageRequest);

        return coursePage.map(course -> CourseDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .level(course.getLevel().getLevel())
                .category(course.getCategory().getValue())
                .thumbnail(course.getThumbnail())
                .objectives(course.getObjectives())
                .requirements(course.getRequirements())
                .skills(course.getSkills())
                .build());
    }

    public Sort createSort(String sortType) {

        if (sortType == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        return switch (sortType) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "rating" -> Sort.by(Sort.Direction.DESC, "averageRating");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");

        };
    }

    public void deleteCourse(Long courseId, UUID userId) {

        User user = userRepository.findByUuid(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다"));

        courseRepository.deleteByInstructorAndId(user, courseId);
    }

    public Page<CourseInstructorDto> getInstructorsCourses(Authentication authentication, int page, int size) {
        UUID userId = UUID.fromString(authentication.getName());

        User user = userRepository.findByUuid(userId).orElseThrow(
            () -> new RuntimeException("유저를 찾을 수 없습니다")
        );

        Pageable pageable = PageRequest.of(page-1, size);

        Page<Course> instructorCourses = courseRepository.findByInstructor(user, pageable);

        // Course List를 CourseInstructorDto List로 변환 & Page 유지
        return instructorCourses.map(course -> {
            // 각 강좌에 대한 리뷰를 조회
            List<Review> reviews = reviewRepository.findByCourse_Id(course.getId());

            // 평균 평점 계산
            double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

            return CourseInstructorDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .averageRating(averageRating)
                .build();
        });
    }
}
