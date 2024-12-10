package com.sesac.backend.courses.controller;

import com.sesac.backend.courses.dto.CourseDto;
import com.sesac.backend.courses.service.CourseService;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequestMapping("/courses")
@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping("")
    public ResponseEntity<?> createCourse(@RequestBody CourseDto request, Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

//        System.out.println("userId : " + userId);
        System.out.println( "요청값 : " + request);

        try {

            courseService.createCourse(userId, request);

            return ResponseEntity.ok(Map.of(
                    "message", "강의 생성이 완료되었습니다"
            ));

        } catch (PersistenceException e) {
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
    public ResponseEntity<?> getAllCourses() {

        try {
            return ResponseEntity.ok(Map.of(
                    "message", "전체 강의 목록 로드에 성공했습니다",
                    "courses", courseService.getAllCourses()
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
}
