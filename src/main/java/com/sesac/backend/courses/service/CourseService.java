package com.sesac.backend.courses.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.dto.*;
import com.sesac.backend.courses.enums.Category;
import com.sesac.backend.courses.enums.Level;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.enrollments.dto.response.RecentEnrollmentDto;
import com.sesac.backend.enrollments.repository.EnrollmentRepository;
import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.lectures.domain.LectureProgress;
import com.sesac.backend.lectures.dto.request.LectureRequest;
import com.sesac.backend.lectures.repository.LectureProgressRepository;
import com.sesac.backend.reviews.domain.Review;
import com.sesac.backend.reviews.dto.response.ReviewResponse;
import com.sesac.backend.reviews.repository.ReviewRepository;
import com.sesac.backend.statistics.dto.CourseIdsDto;
import com.sesac.backend.statistics.service.InstructorStatsService;
import com.sesac.backend.users.domain.InstructorDetail;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.repository.InstructorDetailRepository;
import com.sesac.backend.users.repository.UserRepository;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final InstructorStatsService instructorStatsService;
    private final InstructorDetailRepository instructorDetailRepository;

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
            .instructorId(course.getInstructor().getId())
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
                .map(this::convertToLectureRequest)
                .toList())
            .build();
    }

    private LectureRequest convertToLectureRequest(Lecture entity) {
        LectureProgress progress = lectureProgressRepository.findByLectureId(entity.getId()).orElse(null);

        boolean isCompleted = progress != null && progress.getIsCompleted();

        return LectureRequest.builder()
            .id(entity.getId())
            .courseId(entity.getCourse().getId())
            .title(entity.getTitle())
            .duration(entity.getDuration())
            .orderIndex(entity.getOrderIndex())
            .videoKey(entity.getVideoKey())
            .status(entity.getStatus())
            .isCompleted(isCompleted)
            .build();
    }


    public void updateCourse(Long courseId, UUID userId, CourseDto request) {
        try {
            User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다"));

            Course course = courseRepository.findByInstructorAndId(user, courseId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다"));

            // 1. 유효성 검증 및 Course 데이터 업데이트 로직 (기존과 동일)
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("강좌명은 필수입니다.");
            }
            if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
                throw new IllegalArgumentException("강좌 설명은 필수입니다.");
            }

            Level level;
            try {
                level = Level.from(request.getLevel());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("올바르지 않은 레벨 값입니다: " + request.getLevel());
            }

            Category category;
            try {
                category = Category.valueOf(request.getCategory());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("올바르지 않은 카테고리 값입니다: " + request.getCategory());
            }

            course.setTitle(request.getTitle());
            course.setDescription(request.getDescription());
            course.setLevel(level);
            course.setCategory(category);
            course.setPrice(request.getPrice());
            course.setThumbnail(request.getThumbnail());
            course.setObjectives(request.getObjectives());
            course.setRequirements(request.getRequirements());
            course.setSkills(request.getSkills());


            // 2. lectures 처리
            if (request.getLectures() != null && !request.getLectures().isEmpty()) {
                // lectures 업데이트 로직
                List<Lecture> currentLectures = course.getLectures();
                List<LectureRequest> requestedLectures = request.getLectures();


                // 기존 강의 업데이트 및 신규 강의 추가
                for (LectureRequest lectureRequest : requestedLectures) {
                    boolean found = false;
                    for (Lecture lecture : currentLectures) {
                        if (lectureRequest.getId() != null && lectureRequest.getId().equals(lecture.getId())) {
                            //기존 강의 업데이트
                            lecture.setTitle(lectureRequest.getTitle());
                            // Lecture 엔티티에 content 필드가 있는지 확인 후 적용
                            // lecture.setContent(lectureRequest.getContent());
                            lecture.setDuration(lectureRequest.getDuration());
                            lecture.setOrderIndex(lectureRequest.getOrderIndex());
                            lecture.setVideoKey(lectureRequest.getVideoKey());
                            lecture.setStatus(lectureRequest.getStatus());

                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        //새로운 강의 추가
                        Lecture newLecture = lectureRequest.toEntity(course);
                        currentLectures.add(newLecture);
                    }
                }

                // 요청에 없는 기존 강의 삭제 (선택 사항, 필요한 경우 구현)
                List<Long> requestedLectureIds = requestedLectures.stream()
                    .filter(lectureDto -> lectureDto.getId() != null)
                    .map(LectureRequest::getId)
                    .collect(Collectors.toList());
                currentLectures.removeIf(lecture -> !requestedLectureIds.contains(lecture.getId()));

                course.setLectures(currentLectures);

            }  // lectures 가 null 이거나 비어있다면 기존 데이터 유지

            courseRepository.save(course);
            System.out.println("강좌 수정 완료: " + courseId);
        } catch (Exception e) {
            System.err.println("강좌 수정 실패: " + courseId + ", 오류: " + e);
            if (e.getMessage() != null) {
                System.err.println("에러 메시지 : " + e.getMessage());
            }
            throw e;
        }
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
            .instructorId(course.getInstructor().getId())
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

        CourseIdsDto ids = instructorStatsService.getCourseAndOrderedCourseIds(user);

        Pageable pageable = PageRequest.of(page - 1, size);

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

            int enrollmentCount = Collections.frequency(ids.getSortedCourseIds(), course.getId());

            LocalDateTime createdAt = course.getCreatedAt();

            return CourseInstructorDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .enrollmentCount(enrollmentCount)
                .averageRating(averageRating)
                .createdAt(createdAt.toString())
                .build();
        });
    }

    public List<CourseDto> getFreeCourses() {
        return courseRepository.findByPrice(BigDecimal.ZERO).stream().map(entity -> CourseDto.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .level(entity.getLevel().getLevel())
            .category(entity.getCategory().toString())
            .price(entity.getPrice())
            .thumbnail(entity.getThumbnail())
            .objectives(entity.getObjectives())
            .requirements(entity.getRequirements())
            .skills(entity.getSkills())
            .lectures(entity.getLectures().stream()
                .map(LectureRequest::from)
                .toList())
            .build()).toList();
    }

    public List<RecentEnrollmentDto> getRecentEnrollments(Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        User user = userRepository.findByUuid(userId).orElseThrow(
            () -> new RuntimeException("유저를 찾을 수 없습니다")
        );

        CourseIdsDto ids = instructorStatsService.getCourseAndOrderedCourseIds(user);

        return ids.getDistinctCourseIds().stream()
            .flatMap(courseId -> {

                Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다."));

                return enrollmentRepository.findEnrolledUsersWithDateByCourseId(courseId).stream()
                    .map(enrollment -> RecentEnrollmentDto.builder()
                        .userId(enrollment.getUserId())
                        .username(enrollment.getUsername())
                        .courseName(course.getTitle())
                        .enrolledAt(enrollment.getEnrolledAt())
                        .build());
            })
            .sorted((e1, e2) -> LocalDateTime.parse(e2.getEnrolledAt())
                .compareTo(LocalDateTime.parse(e1.getEnrolledAt())))
            .limit(5)
            .collect(Collectors.toList());
    }

    public List<ReviewResponse> getRecentReviews(Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            User user = userRepository.findByUuid(userId).orElseThrow(
                () -> new RuntimeException("유저를 찾을 수 없습니다")
            );

            log.info("User found: {}", user);

            List<Course> courses = courseRepository.findByInstructor(user);
            log.info("Found courses: {}", courses);

            return courses.stream()
                .flatMap(course -> {
                    List<Review> reviews = reviewRepository.findByCourse_Id(course.getId());
                    log.info("Reviews for course {}: {}", course.getId(), reviews);

                    return reviews.stream()
                        .map(review -> ReviewResponse.builder()
                            .id(review.getId())
                            .writer(review.getWriter().getNickname())
                            .content(review.getContent())
                            .rating(review.getRating())
                            .courseName(course.getTitle())
                            .createdAt(review.getCreatedAt().toString())
                            .build());
                })
                .sorted((r1, r2) -> LocalDateTime.parse(r2.getCreatedAt())
                    .compareTo(LocalDateTime.parse(r1.getCreatedAt())))
                .limit(3)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error in getRecentReviews: ", e);
            throw e;
        }
    }

    public List<InstructorInfoDTO> getInstructorInfo(Long instructorId) {
        try {
            User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 강사를 찾을 수 없습니다: " + instructorId));

            InstructorDetail instructorDetail = instructorDetailRepository.findByUser_id(instructorId);

            if (instructorDetail == null) {
                throw new NoSuchElementException("해당 ID의 강사 상세 정보를 찾을 수 없습니다: " + instructorId);
            }

            InstructorInfoDTO instructorInfoDTO = new InstructorInfoDTO(
                user.getNickname(),
                user.getId(),
                instructorDetail.getIntroduction(),
                instructorDetail.getTechStack(),
                instructorDetail.getWebsiteUrl(),
                instructorDetail.getLinkedinUrl(),
                instructorDetail.getGithubUrl()

            );
            List<InstructorInfoDTO> instructorList = new ArrayList<>();
            instructorList.add(instructorInfoDTO);
            System.out.println("강사 정보 : " + instructorList);
            return instructorList;

        } catch (NoSuchElementException e) {
            System.out.println("강사 정보 조회 실패(NoSuchElementException) : " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("강사 정보 조회 실패 (Exception) : " + e);
            throw new RuntimeException("강사 정보 조회에 실패했습니다.", e);
        }
    }


    // 25.01.03 홍인표 작성. 수강 중인 강좌의 진행률을 반환하는 메서드
    @Transactional(readOnly = true)
    public CourseProgressResponse getCourseProgress(Long courseId, UUID userId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다."));

        List<Lecture> lectures = course.getLectures();
        int totalLectures = lectures.size();

        // 완료된 강의 수 계산
        int completedLectures = (int) lectures.stream()
            .filter(lecture -> {
                LectureProgress progress = lectureProgressRepository
                    .findByLectureIdAndStudent_Uuid(lecture.getId(), userId)
                    .orElse(null);
                return progress != null && progress.getIsCompleted();
            })
            .count();

        double progressRate = totalLectures > 0
            ? (double) completedLectures / totalLectures * 100
            : 0;

        return CourseProgressResponse.builder()
            .courseId(courseId)
            .totalLectures(totalLectures)
            .completedLectures(completedLectures)
            .progressRate(progressRate)
            .build();
    }
}
