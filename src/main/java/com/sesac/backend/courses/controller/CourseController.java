package com.sesac.backend.courses.controller;

import com.sesac.backend.courses.dto.CourseDto;
import com.sesac.backend.courses.dto.CourseInstructorDto;
import com.sesac.backend.courses.dto.CourseSearchCriteria;
import com.sesac.backend.courses.service.CourseService;
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
//            e.printStackTrace();
//            System.out.println("오류 : " + e.getMessage());
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
            List<CourseInstructorDto> courseDtos = courseService.getInstructorsCourses(authentication, page, size);

            if (courseDtos.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "message", "등록된 강좌가 없습니다.",
                    "myCourseList", courseDtos
                ));
            }

            return ResponseEntity.ok(Map.of(
                "message", "강좌 목록 호출 성공",
                "myCourseList", courseDtos
            ));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(Map.of());
        }
    }

    //특정 강의 수강평 조회
    //api endpoint 표준 때문에 courseCont에 작성했습니다. gnuke
    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<?> getAllReviewsInCourse(@PathVariable Long courseId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        try{
            List<ReviewResponse> reviews = reviewService.getReviews(courseId, page, size);

            if (reviews.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "message", "수강평이 없습니다.",
                    "reviews", reviews
                ));
            }

            return ResponseEntity.ok(Map.of(
                "message", "수강평 목록 호출 성공",
                "reviews", reviews
            ));
        }catch (EntityNotFoundException e){
            return ResponseEntity.noContent().build();
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

}
