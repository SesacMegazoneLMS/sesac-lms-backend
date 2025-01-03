package com.sesac.backend.courses.controller;

import com.sesac.backend.courses.dto.CourseDto;
import com.sesac.backend.courses.dto.CourseInstructorDto;
import com.sesac.backend.courses.dto.CourseProgressResponse;
import com.sesac.backend.courses.dto.CourseSearchCriteria;
import com.sesac.backend.courses.service.CourseService;
import com.sesac.backend.enrollments.dto.response.RecentEnrollmentDto;
import com.sesac.backend.reviews.dto.response.PageResponse;
import com.sesac.backend.reviews.dto.response.ReviewResponse;
import com.sesac.backend.reviews.dto.response.ReviewStatus;
import com.sesac.backend.reviews.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequestMapping("/courses")
@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<?> createCourse(@RequestBody CourseDto request, Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        try {

            Long courseId = courseService.createCourse(userId, request);

            return ResponseEntity.ok(Map.of(
                    "message", "강의 생성이 완료되었습니다",
                    "courseId", courseId
            ));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "동일한 이름의 강의가 존재합니다"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getCourses(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        try {
            CourseSearchCriteria criteria = CourseSearchCriteria.builder()
                    .sort(sort)
                    .category(category)
                    .level(level)
                    .search(search)
                    .build();

            Page<CourseDto> courses = courseService.searchCourses(criteria, PageRequest.of(page, size));


            return ResponseEntity.ok(Map.of(
                    "message", "전체 강의 목록 로드에 성공했습니다",
                    "courses", courses.getContent(),
                    "totalPages", courses.getTotalPages(),
                    "totalElements", courses.getTotalElements(),
                    "currentPage", courses.getNumber()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> courseDetails(@PathVariable Long courseId) {

        try {

            CourseDto courseDetails = courseService.getCourseByCourseId(courseId);

            return ResponseEntity.ok(Map.of(
                    "message", "강의 상세 정보 로드에 성공했습니다",
                    "courseDetails", courseDetails
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable Long courseId, @RequestBody CourseDto request, Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        try {

            courseService.updateCourse(courseId, userId, request);

            return ResponseEntity.ok(Map.of(
                    "message", "강의 수정이 완료되었습니다"
            ));

        } catch (PersistenceException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "동일한 이름의 강의가 존재합니다"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId, Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        try {

            courseService.deleteCourse(courseId, userId);

            return ResponseEntity.ok(Map.of(
                    "message", "강의가 삭제되었습니다"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // getMyCourse(Instructor)
    @GetMapping("/instructor/me")
    public ResponseEntity<?> getMyCourses(Authentication authentication,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        try{
            Page<CourseInstructorDto> courseDtos = courseService.getInstructorsCourses(authentication, page, size);

            if (courseDtos.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "message", "등록된 강좌가 없습니다.",
                    "myCourseList", Collections.emptyList(),
                    "totalPages", 0,
                    "totalItems", 0,
                    "currentPage", page
                ));
            }

            return ResponseEntity.ok(Map.of(
                "message", "강좌 목록 호출 성공",
                "myCourseList", courseDtos.getContent(),
                "totalPages", courseDtos.getTotalPages(),
                "totalItems", courseDtos.getTotalElements(),
                "currentPage", courseDtos.getNumber() + 1
            ));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(Map.of());
        }
    }

    // 최근 수강 신청 조회 호출 추가
    @GetMapping("/instructor/me/recentenrollments")
    public ResponseEntity<?> getRecentEnrollments(Authentication authentication){

        try {
            List<RecentEnrollmentDto> recentEnrollments = courseService.getRecentEnrollments(authentication);

            if (recentEnrollments.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "message", "등록된 강좌 및 최근 수강 신청된 강의가 없습니다.",
                        "recentEnrollments", Collections.emptyList()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "강좌 목록 및 최근 수강 신청된 강의 목록 호출 성공",
                    "recentEnrollments", recentEnrollments
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of());
        }
    }

    // 최근 리뷰 조회 호출 추가
    @GetMapping("/instructor/me/recentreviews")
    public ResponseEntity<?> getRecentReviews(Authentication authentication){

        try {
            List<ReviewResponse> recentReviews = courseService.getRecentReviews(authentication);

            if (recentReviews.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "message", "등록된 강좌 및 최근 리뷰가 없습니다.",
                        "recentReviews", Collections.emptyList()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "강좌 목록 및 최근 리뷰 목록 호출 성공",
                    "recentReviews", recentReviews
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of());
        }

    }

    //특정 강의 수강평 조회
    //api endpoint 표준 때문에 courseCont에 작성했습니다. gnuke
    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<?> getAllReviewsInCourse(@PathVariable Long courseId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            PageResponse<ReviewResponse> pageResponse = reviewService.getReviews(courseId, page, size);
            if (pageResponse.getContent().isEmpty()) {
                return ResponseEntity.ok(PageResponse.<ReviewResponse>builder()
                    .content(pageResponse.getContent())
                    .totalPages(pageResponse.getTotalPages())
                    .totalElements(pageResponse.getTotalElements())
                    .currentPage(pageResponse.getCurrentPage())
                    .build());
            }
            return ResponseEntity.ok(pageResponse);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "서버 에러"));
        }
    }

    // 강의별 수강평 평균, 수강평 수 조회
    @GetMapping("/{courseId}/scores")
    public ResponseEntity<?> getScoresInfo(@PathVariable Long courseId){
        try{
            ReviewStatus reviewStatus = reviewService.getScoresInfo(courseId);

            return ResponseEntity.ok(Map.of(
               "totalCount", reviewStatus.getReviewCount(),
               "avarageRating", reviewStatus.getAverageRating()
            ));
        }catch (EntityNotFoundException e){
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/free")
    public ResponseEntity<List<CourseDto>> getFreeCourses() {
        try {
            return ResponseEntity.ok(courseService.getFreeCourses());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 25.01.03 홍인표 작성. 수강 중인 강좌의 진행률을 반환하는 메서드
    @GetMapping("/{courseId}/progress")
    public ResponseEntity<CourseProgressResponse> getCourseProgress(
            @PathVariable Long courseId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        CourseProgressResponse progress = courseService.getCourseProgress(courseId, userId);
        return ResponseEntity.ok(progress);
    }
}
